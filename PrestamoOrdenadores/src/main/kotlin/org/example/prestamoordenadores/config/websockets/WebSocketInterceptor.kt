package org.example.prestamoordenadores.config.websockets

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

class WebSocketInterceptor : HandshakeInterceptor {
    @Throws(
        Exception::class
    )
    public override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        if (authentication != null) {
            attributes.put("username", authentication.name)
            return true
        }

        return false
    }

    public override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: java.lang.Exception?
    ) {
        // No se realiza ninguna lógica posterior
    }
}