package org.example.prestamoordenadores.config.websockets

import org.lighthousegames.logging.logging
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

private val log = logging()

class WebSocketInterceptor : HandshakeInterceptor {
    @Throws(
        Exception::class
    )
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        val requestUri = request.uri

        log.info { "[WebSocketInterceptor] Interceptando handshake para URI: $requestUri" }

        if (authentication != null && authentication.isAuthenticated) {
            val username = authentication.name
            log.info { "[WebSocketInterceptor] Authentication encontrada: Name='${username}', IsAuthenticated=${authentication.isAuthenticated}, Authorities=${authentication.authorities}" }
            if (username != null && username != "anonymousUser") {
                attributes["username"] = username
                log.info { "[WebSocketInterceptor] Atributo 'username' establecido a '$username' para la sesión WebSocket." }
                return true
            } else {
                log.warn { "[WebSocketInterceptor] Usuario autenticado es 'anonymousUser' o nombre nulo. URI: $requestUri. No se establecerá 'username' específico." }
                return false;
            }
        } else {
            log.warn { "[WebSocketInterceptor] No se encontró Authentication válida en SecurityContextHolder. URI: $requestUri. Authentication: $authentication" }
            return false
        }
    }

    public override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: java.lang.Exception?
    ) {
    }
}