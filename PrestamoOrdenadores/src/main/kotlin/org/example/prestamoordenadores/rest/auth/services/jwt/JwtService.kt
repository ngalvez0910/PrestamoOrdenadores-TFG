package org.example.prestamoordenadores.rest.auth.services.jwt

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
interface JwtService {
    fun extractUserName(token: String?): String?

    fun generateToken(userDetails: UserDetails?): String?

    fun isTokenValid(token: String?, userDetails: UserDetails?): Boolean
}