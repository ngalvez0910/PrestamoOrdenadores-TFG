package org.example.prestamoordenadores.config.websockets

import org.lighthousegames.logging.logging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

private val log = logging()

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketIncidenciasHandler(), "/ws/incidencias")
            .addInterceptors(WebSocketInterceptor());
        registry.addHandler(webSocketSancionesHandler(), "/ws/sanciones")
            .addInterceptors(WebSocketInterceptor());
        registry.addHandler(webSocketPrestamosHandler(), "/ws/prestamos")
            .addInterceptors(WebSocketInterceptor());

        log.info { "WebSocket handlers registrados con éxito" }
    }

    @Bean
    fun webSocketIncidenciasHandler(): WebSocketHandler {
        return WebSocketHandler("Incidencias")
    }

    @Bean
    fun webSocketSancionesHandler(): WebSocketHandler {
        return WebSocketHandler("Sanciones")
    }

    @Bean
    fun webSocketPrestamosHandler(): WebSocketHandler {
        return WebSocketHandler("Prestamos")
    }
}