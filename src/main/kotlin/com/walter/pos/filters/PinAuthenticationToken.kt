package com.walter.pos.filters

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class PinAuthenticationToken(private val pin: String) : AbstractAuthenticationToken(null as Collection<GrantedAuthority>?){

    override fun getCredentials(): Any = pin
    override fun getPrincipal(): Any = pin

    init {
        isAuthenticated = false
    }
}