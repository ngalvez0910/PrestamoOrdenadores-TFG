package org.example.prestamoordenadores.config.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.prestamoordenadores.rest.auth.services.jwt.JwtService
import org.example.prestamoordenadores.rest.users.services.CustomUserDetailsService
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

private val log = logging()

@Component
class JwtAuthenticationFilter
@Autowired constructor(
    private val jwtService: JwtService,
    private val authUsersService: CustomUserDetailsService
): OncePerRequestFilter(){

    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse, @NonNull filterChain: FilterChain
    ) {
        log.debug { "Iniciando el filtro de autenticación" }
        val authHeader = request.getHeader("Authorization")
        var userDetails: UserDetails?
        var userName: String?

        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            log.debug { "No se ha encontrado cabecera de autenticación, se ignora" }
            filterChain.doFilter(request, response)
            return
        }

        log.debug { "Se ha encontrado cabecera de autenticación, se procesa" }
        val jwt: String = authHeader.substring(7)
        try {
            userName = jwtService.extractUserName(jwt)
        } catch (e: Exception) {
            log.debug { "Token no válido" }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o no válido")
            return
        }

        log.info { "Usuario autenticado: $userName" }
        if (StringUtils.hasText(userName)
            && SecurityContextHolder.getContext().authentication == null
        ) {
            log.debug { "Comprobando usuario y token" }
            try {
                userDetails = authUsersService.loadUserByUsername(userName.toString())
            } catch (e: Exception) {
                log.debug { "Usuario no encontrado: $userName" }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado")
                return
            }
            authUsersService.loadUserByUsername(userName.toString())
            log.debug { "Usuario encontrado: $userDetails" }
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.debug { "JWT válido" }

                val roles = jwtService.extractRoles(jwt)
                val authorities = roles.map { SimpleGrantedAuthority(it) }

                val context = SecurityContextHolder.createEmptyContext()
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                context.authentication = authToken
                SecurityContextHolder.setContext(context)
            }

        }
        filterChain.doFilter(request, response)
    }
}