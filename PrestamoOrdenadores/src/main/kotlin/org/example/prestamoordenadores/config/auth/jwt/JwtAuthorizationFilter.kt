package org.example.prestamoordenadores.config.auth.jwt

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import org.example.prestamoordenadores.rest.users.services.UserService
import kotlinx.coroutines.runBlocking
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.prestamoordenadores.config.auth.UserDetails
import org.example.prestamoordenadores.rest.users.mappers.UserMapper

class JwtAuthorizationFilter(
    private val jwtTokenUtil: JwtTokenUtils,
    private val service: UserService,
    private val userMapper: UserMapper,
    authManager: AuthenticationManager,
) : BasicAuthenticationFilter(authManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        logger.info { "Filtrando" }
        val header = req.getHeader(AUTHORIZATION.toString())
        if (header == null || !header.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            chain.doFilter(req, res)
            return
        }
        getAuthentication(header.substring(7))?.also {
            SecurityContextHolder.getContext().authentication = it
        }
        chain.doFilter(req, res)
    }

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken? = runBlocking {
        logger.info { "Filtrando..." }

        if (!jwtTokenUtil.isTokenValid(token)) return@runBlocking null
        val userId = jwtTokenUtil.getUserIdFromJwt(token)

        val result = service.getUserByGuidAdmin(userId)

        result.onSuccess { userResponse ->
            val user = userMapper.toUserFromResponseAdmin(userResponse)

            val customUserDetails = UserDetails(user)
            return@runBlocking UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.authorities
            )
        }.onFailure { error ->
            logger.error { "Error al obtener el usuario: ${error.message}" }
        }

        return@runBlocking null
    }
}