package org.example.prestamoordenadores.config.auth.jwt

import org.lighthousegames.logging.logging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.prestamoordenadores.rest.users.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.users.models.User

private val logger = logging()

class JwtAuthenticationFilter(
    private val jwtTokenUtil: JwtTokenUtils,
    private val authenticationManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(req: HttpServletRequest, response: HttpServletResponse): Authentication {
        logger.info { "Intentando autenticar" }

        val credentials = ObjectMapper().readValue(req.inputStream, UserLoginRequest::class.java)
        val auth = UsernamePasswordAuthenticationToken(
            credentials.email,
            credentials.password,
        )
        return authenticationManager.authenticate(auth)
    }

    override fun successfulAuthentication(
        req: HttpServletRequest?, res: HttpServletResponse, chain: FilterChain?,
        auth: Authentication
    ) {
        logger.info { "Autenticación correcta" }

        // val username = (auth.principal as Usuario).username
        // val token: String = jwtTokenUtil.generateToken(username)
        val user = auth.principal as User
        val token: String = jwtTokenUtil.generateToken(user)
        res.addHeader("Authorization", token)
        // Authorization
        res.addHeader("Access-Control-Expose-Headers", JwtTokenUtils.TOKEN_HEADER)
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        logger.info { "Autenticación incorrecta" }

        val error = BadCredentialsError()
        response.status = error.status
        response.contentType = "application/json"
        response.writer.append(error.toString())
    }

}

private data class BadCredentialsError(
    val timestamp: Long = Date().time,
    val status: Int = 401,
    val message: String = "Usuario o contraseña incorrectos",
) {
    override fun toString(): String {
        return ObjectMapper().writeValueAsString(this)
    }
}