package com.walter.pos.filters

import com.walter.pos.repository.UserRepository
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class PinAuthenticationProvider(private val userRepo: UserRepository
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val inputPin = authentication.principal.toString()

        val user = userRepo.findByPin(inputPin).orElseThrow { BadCredentialsException("Invalid Pin Provided") }
        return UsernamePasswordAuthenticationToken(
            user,
            null,
            user.authorities
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return PinAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}