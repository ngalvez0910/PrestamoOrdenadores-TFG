package org.example.prestamoordenadores.config.websockets

import org.lighthousegames.logging.logging
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

private val log = logging()

/**
 * Interceptor para el handshake de WebSocket.
 *
 * Esta clase intercepta las solicitudes de handshake de WebSocket para extraer la información de autenticación
 * del usuario y añadirla a los atributos de la sesión WebSocket. Esto permite que los manejadores de WebSocket
 * accedan al nombre de usuario autenticado.
 *
 * @author Natalia González Álvarez
 */
class WebSocketInterceptor : HandshakeInterceptor {

    /**
     * Se invoca antes de que se realice el handshake de WebSocket.
     *
     * Este método verifica la autenticación del usuario en el [SecurityContextHolder].
     * Si el usuario está autenticado y no es "anonymousUser", su nombre de usuario se añade a los
     * atributos de la sesión WebSocket.
     *
     * @param request La [ServerHttpRequest] entrante.
     * @param response La [ServerHttpResponse] saliente.
     * @param wsHandler El [WebSocketHandler] que manejará la conexión.
     * @param attributes Los atributos de la sesión WebSocket a los que se puede añadir información.
     * @return `true` si el handshake debe continuar, `false` si debe ser denegado.
     * @throws Exception Si ocurre un error durante el procesamiento.
     * @author Natalia González Álvarez
     */
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

    /**
     * Se invoca después de que se ha realizado el handshake de WebSocket (independientemente de si fue exitoso o no).
     *
     * Este método no realiza ninguna acción en la implementación actual.
     *
     * @param request La [ServerHttpRequest] entrante.
     * @param response La [ServerHttpResponse] saliente.
     * @param wsHandler El [WebSocketHandler] que manejará la conexión.
     * @param exception Cualquier excepción que haya ocurrido durante el handshake (nulo si no hubo).
     * @author Natalia González Álvarez
     */
    public override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: java.lang.Exception?
    ) {
    }
}