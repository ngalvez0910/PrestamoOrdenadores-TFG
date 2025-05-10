package org.example.prestamoordenadores.config.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.prestamoordenadores.rest.auth.services.jwt.JwtService
import org.example.prestamoordenadores.rest.users.services.CustomUserDetailsService
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

private val log = logging()

@Component
class JwtAuthenticationFilter
@Autowired constructor(
    private val jwtService: JwtService,
    authUserService: CustomUserDetailsService
) : OncePerRequestFilter() {
    private val authUsersService: CustomUserDetailsService = authUserService

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse, @NonNull filterChain: FilterChain
    ) {
        log.info { "Iniciando el filtro de autenticación para URI: ${request.requestURI}" }

        if (request.servletPath.equals("/auth/signin") || request.servletPath.equals("/auth/signup")) {
            log.info { "Petición a ruta de autenticación/registro, se ignora el filtro JWT" }
            filterChain.doFilter(request, response)
            return
        }

        var jwt: String? = null
        val authHeader = request.getHeader("Authorization")

        if (StringUtils.hasText(authHeader) && StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            log.info { "Token encontrado en encabezado 'Authorization'." }
            jwt = authHeader.substring(7)
        } else {
            val tokenParam = request.getParameter("token")
            if (StringUtils.hasText(tokenParam) && request.requestURI.startsWith("/ws/")) {
                log.info { "Token encontrado en parámetro de query 'token' para URI WebSocket: ${request.requestURI}" }
                jwt = tokenParam
            }
        }

        if (!StringUtils.hasText(jwt)) {
            log.info { "No se encontró token JWT en header ni en parámetro de query (si aplica para WS). Continuando cadena de filtros." }
            filterChain.doFilter(request, response)
            return
        }

        log.info { "Token JWT encontrado (de header o query param), se procesa." }
        var userName: String?
        try {
            userName = jwtService.extractUserName(jwt!!)
        } catch (e: Exception) {
            log.warn { "Token JWT no válido o expirado: ${e.message}" }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o no válido")
            return
        }

        if (StringUtils.hasText(userName) && SecurityContextHolder.getContext().authentication == null) {
            log.info { "Usuario '$userName' extraído del token. SecurityContext está vacío, procediendo a cargar UserDetails." }
            val userDetails: UserDetails
            try {
                userDetails = authUsersService.loadUserByUsername(userName!!)
            } catch (e: Exception) {
                log.warn { "Usuario no encontrado por UserDetailsService: $userName. Error: ${e.message}" }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado o no encontrado")
                return
            }

            log.info { "UserDetails encontrado para '$userName': ${userDetails.username}, Authorities: ${userDetails.authorities}" }

            if (jwtService.isTokenValid(jwt!!, userDetails)) {
                log.info { "Token JWT es válido para usuario '$userName'." }
                val context: SecurityContext = SecurityContextHolder.createEmptyContext()
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                context.authentication = authToken
                SecurityContextHolder.setContext(context)
                log.info { "SecurityContext actualizado con la autenticación para '$userName'." }
            } else {
                log.warn { "Validación del token JWT falló para usuario '$userName'." }
            }
        } else if (SecurityContextHolder.getContext().authentication != null) {
            log.info { "SecurityContext ya contiene una autenticación para '${SecurityContextHolder.getContext().authentication.name}'. No se procesará el token JWT de nuevo en esta petición." }
        }


        filterChain.doFilter(request, response)
    }
}