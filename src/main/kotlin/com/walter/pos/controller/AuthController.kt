package com.walter.pos.controller

import com.walter.pos.dtos.LoginRequest
import com.walter.pos.dtos.LoginResponse
import com.walter.pos.entities.User
import com.walter.pos.service.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController(private val authenticationManager: AuthenticationManager, private val jwtService: JwtService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> { val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.pin))

        val user = authentication.principal as User
        val jwtToken = jwtService.generateToken(user)

        val response = LoginResponse(
            token = jwtToken,
            firstName = user.firstName,
            lastName = user.lastName
        )

        return ResponseEntity.ok(response)
    }
}