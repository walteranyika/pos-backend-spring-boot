package com.walter.pos.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.walter.pos.dtos.ErrorResponse
import com.walter.pos.service.JwtService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        try {
            val jwt: String = authHeader.substring(7)
            val username: String = jwtService.extractUsername(jwt)

            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = this.userDetailsService.loadUserByUsername(username)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    ).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
            filterChain.doFilter(request, response)
        }catch (ex: ExpiredJwtException){
            handleException(response, request, "JWT token has expired", HttpServletResponse.SC_UNAUTHORIZED)
        }catch (ex: JwtException){
            handleException(response, request, "Invalid JWT Token", HttpServletResponse.SC_UNAUTHORIZED)
        }catch (ex: Exception){
            handleException(response, request, "Authentication error", HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

    private fun handleException(response: HttpServletResponse, request: HttpServletRequest, message: String, status: Int){
        response.status = status
        response.contentType = "application/json"
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = status,
            error = "Unauthorized",
            message = message,
            path = request.servletPath
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}