package org.example.prestamoordenadores.config.websockets

import org.lighthousegames.logging.logging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

private val log = logging()

/**
 * Configuración de WebSockets para la aplicación.
 *
 * Habilita el soporte para WebSockets y registra los diferentes manejadores de WebSocket
 * para distintas funcionalidades como incidencias, sanciones, préstamos y notificaciones globales.
 *
 * @author Natalia González Álvarez
 */
@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    /**
     * Registra los manejadores de WebSocket en el registro proporcionado.
     *
     * Cada manejador se asocia a una URL específica y se le añade un interceptor.
     * El manejador de notificaciones globales (`/ws/notificaciones`) permite cualquier origen.
     *
     * @param registry El [WebSocketHandlerRegistry] donde se registrarán los manejadores.
     * @author Natalia González Álvarez
     */
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketIncidenciasHandler(), "/ws/incidencias")
            .addInterceptors(WebSocketInterceptor());
        registry.addHandler(webSocketSancionesHandler(), "/ws/sanciones")
            .addInterceptors(WebSocketInterceptor());
        registry.addHandler(webSocketPrestamosHandler(), "/ws/prestamos")
            .addInterceptors(WebSocketInterceptor());
        registry.addHandler(webSocketGlobalHandler(), "/ws/notificaciones")
            .addInterceptors(WebSocketInterceptor())
            .setAllowedOrigins("*");

        log.info { "WebSocket handlers registrados con éxito" }
    }

    /**
     * Crea y devuelve un [WebSocketHandler] para las incidencias.
     *
     * @return Una instancia de [WebSocketHandler] configurada para "Incidencias".
     * @author Natalia González Álvarez
     */
    @Bean
    fun webSocketIncidenciasHandler(): WebSocketHandler {
        return WebSocketHandler("Incidencias")
    }

    /**
     * Crea y devuelve un [WebSocketHandler] para las sanciones.
     *
     * @return Una instancia de [WebSocketHandler] configurada para "Sanciones".
     * @author Natalia González Álvarez
     */
    @Bean
    fun webSocketSancionesHandler(): WebSocketHandler {
        return WebSocketHandler("Sanciones")
    }

    /**
     * Crea y devuelve un [WebSocketHandler] para los préstamos.
     *
     * @return Una instancia de [WebSocketHandler] configurada para "Prestamos".
     * @author Natalia González Álvarez
     */
    @Bean
    fun webSocketPrestamosHandler(): WebSocketHandler {
        return WebSocketHandler("Prestamos")
    }

    /**
     * Crea y devuelve un [WebSocketHandler] para las notificaciones globales.
     *
     * @return Una instancia de [WebSocketHandler] configurada para "NotificacionesGlobales".
     * @author Natalia González Álvarez
     */
    @Bean
    fun webSocketGlobalHandler(): WebSocketHandler {
        return WebSocketHandler("NotificacionesGlobales")
    }
}