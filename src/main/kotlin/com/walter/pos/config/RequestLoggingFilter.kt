package com.walter.pos.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import java.nio.charset.StandardCharsets

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    companion object {
        private val logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Wrap the request to make its body readable multiple times
        val wrappedRequest = ContentCachingRequestWrapper(request)

        // Proceed with the filter chain (and controller processing)
        filterChain.doFilter(wrappedRequest, response)

        // After the request has been handled, log the body
        val requestBody = String(wrappedRequest.contentAsByteArray, StandardCharsets.UTF_8)

        if (requestBody.isNotBlank()) {
            Companion.logger.info(
                "Request Body: [method={}, uri={}, body={}]",
                wrappedRequest.method,
                wrappedRequest.requestURI,
                requestBody.replace("\\s".toRegex(), "") // Remove whitespace for cleaner logs
            )
        }
    }
}