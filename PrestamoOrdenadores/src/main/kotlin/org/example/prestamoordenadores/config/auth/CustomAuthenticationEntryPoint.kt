package org.example.prestamoordenadores.config.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access - Please authenticate.")
    }
}