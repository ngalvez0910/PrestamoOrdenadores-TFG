package org.example.prestamoordenadores.config.websockets

import org.lighthousegames.logging.logging
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

private val log = logging()

/**
 * Manejador de WebSocket para gestionar conexiones, enviar mensajes y mantener un registro de sesiones.
 *
 * Esta clase extiende [TextWebSocketHandler] para procesar mensajes de texto y
 * también implementa [SubProtocolCapable] para especificar subprotocolos soportados.
 * Además, implementa la interfaz [WebSocketSender] para la funcionalidad de envío de mensajes.
 *
 * @property entity El nombre de la entidad asociada a este manejador de WebSocket (ej. "Incidencias", "Sanciones").
 * @author Natalia González Álvarez
 */
class WebSocketHandler(
    private val entity: String?
) : TextWebSocketHandler(), SubProtocolCapable, WebSocketSender {
    private val sessions: MutableSet<WebSocketSession> = CopyOnWriteArraySet()

    private val userSessionsMap: MutableMap<String?, WebSocketSession?> =
        ConcurrentHashMap<String?, WebSocketSession?>()

    /**
     * Se invoca después de que se ha establecido una nueva conexión WebSocket.
     *
     * Registra la sesión, intenta asociarla a un nombre de usuario (si está disponible en los atributos de la sesión)
     * y envía un mensaje de saludo inicial.
     *
     * @param session La sesión WebSocket recién establecida.
     * @throws Exception Si ocurre un error durante el establecimiento de la conexión o el envío del mensaje.
     * @author Natalia González Álvarez
     */
    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info { "[WS-HANDLER ($entity)] Conexión entrante. SessionId: ${session.id}, URI: ${session.uri}, RemoteAddress: ${session.remoteAddress}, Attributes: ${session.attributes}" }

        val username = session.attributes["username"] as? String

        if (username != null) {
            userSessionsMap[username] = session
            log.info { "[WS-HANDLER ($entity)] Usuario '$username' (SessionId: ${session.id}) AÑADIDO/ACTUALIZADO en userSessionsMap. Tamaño del mapa ahora: ${userSessionsMap.size}. Claves: ${userSessionsMap.keys}" }
        } else {
            log.warn { "[WS-HANDLER ($entity)] ATRIBUTO 'username' NO ENCONTRADO en sesión ${session.id}. Usuario NO AÑADIDO a userSessionsMap." }
        }

        sessions.add(session)
        val message = TextMessage("Web socket: $entity - LoanTech")
        log.info { "[WS-HANDLER ($entity)] Enviando saludo a SessionId: ${session.id}" }
        try {
            session.sendMessage(message)
        } catch (e: IOException) {
            log.error(e) { "[WS-HANDLER ($entity)] IOException al enviar saludo a ${session.id}" }
        }
    }

    /**
     * Se invoca después de que se ha cerrado una conexión WebSocket.
     *
     * Elimina la sesión del conjunto de sesiones activas y, si estaba asociada a un usuario, la elimina del mapa de usuarios.
     *
     * @param session La sesión WebSocket que ha sido cerrada.
     * @param status El estado de cierre de la conexión.
     * @throws Exception Si ocurre un error durante el cierre de la conexión.
     * @author Natalia González Álvarez
     */
    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val username = session.attributes["username"] as? String
        log.info { "[WS-HANDLER ($entity)] Conexión CERRADA. SessionId: ${session.id}, Usuario: '$username', Status: $status. Atributos: ${session.attributes}" }

        if (username != null) {
            val removedSession = userSessionsMap.remove(username)
            if (removedSession != null) {
                log.info { "[WS-HANDLER ($entity)] Usuario '$username' (SessionId: ${session.id}) ELIMINADO de userSessionsMap debido a cierre. Tamaño del mapa ahora: ${userSessionsMap.size}." }
            } else {
                log.warn { "[WS-HANDLER ($entity)] Usuario '$username' (SessionId: ${session.id}) no encontrado en userSessionsMap al intentar eliminar por cierre (quizás nunca se añadió o ya se eliminó)." }
            }
        }
        sessions.remove(session)
    }

    /**
     * Envía un mensaje de texto a todas las sesiones WebSocket activas.
     *
     * @param message El mensaje de texto a enviar.
     * @throws IOException Si ocurre un error al enviar el mensaje.
     * @author Natalia González Álvarez
     */
    @Throws(IOException::class)
    override fun sendMessage(message: String?) {
        log.info { "Enviar mensaje de cambios en la entidad: $entity : $message" }

        for (session in sessions) {
            if (session.isOpen) {
                log.info { "Servidor WS envía: $message" }
                message?.let { session.sendMessage(TextMessage(it)) }
            }
        }
    }

    /**
     * Envía un mensaje de texto a una sesión WebSocket específica asociada a un nombre de usuario.
     *
     * @param username El nombre de usuario del destinatario.
     * @param message El mensaje de texto a enviar.
     * @throws IOException Si ocurre un error al enviar el mensaje.
     * @author Natalia González Álvarez
     */
    @Throws(IOException::class)
    override fun sendMessageToUser(username: String?, message: String?) {
        if (username == null || message == null) {
            log.warn { "[WS-HANDLER ($entity)] Intento de enviar mensaje con username o payload nulo. Username: $username" }
            return
        }
        log.info { "[WS-HANDLER ($entity)] Intentando enviar a usuario '$username'. Payload (primeros 100 chars): ${message.take(100)}..." }
        val session: WebSocketSession? = userSessionsMap[username]

        if (session != null && session.isOpen) {
            try {
                session.sendMessage(TextMessage(message))
                log.info { "[WS-HANDLER ($entity)] MENSAJE ENVIADO CORRECTAMENTE a '$username' (SessionId: ${session.id})." }
            } catch (e: IOException) {
                log.error(e) { "[WS-HANDLER ($entity)] IOException al enviar mensaje a '$username' (SessionId: ${session.id})"}
            }
        } else {
            if (session == null) {
                log.warn { "[WS-HANDLER ($entity)] SESIÓN NO ENCONTRADA para usuario '$username' en userSessionsMap. Mensaje NO ENVIADO. Usuarios actualmente en mapa: ${userSessionsMap.keys}" }
            } else {
                log.warn { "[WS-HANDLER ($entity)] Sesión encontrada para '$username' (SessionId: ${session.id}) PERO ESTÁ CERRADA. Mensaje NO ENVIADO. Eliminando del mapa." }
                userSessionsMap.remove(username)
            }
        }
    }

    /**
     * Maneja los mensajes de texto entrantes de una sesión WebSocket.
     *
     * Actualmente, este método no implementa ninguna lógica específica para el manejo de mensajes recibidos.
     *
     * @param session La sesión WebSocket de la que se recibió el mensaje.
     * @param message El mensaje de texto recibido.
     * @throws Exception Si ocurre un error al manejar el mensaje.
     * @author Natalia González Álvarez
     */
    @Throws(
        Exception::class
    )
    protected override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
    }

    /**
     * Maneja errores de transporte que ocurren en una sesión WebSocket.
     *
     * Actualmente, este método no implementa ninguna lógica específica para el manejo de errores.
     *
     * @param session La sesión WebSocket donde ocurrió el error.
     * @param exception La excepción que representa el error de transporte.
     * @throws Exception Si ocurre un error al manejar el error de transporte.
     * @author Natalia González Álvarez
     */
    @Throws(
        Exception::class
    )
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    }

    /**
     * Devuelve una lista de subprotocolos soportados por este manejador de WebSocket.
     *
     * @return Una lista mutable de cadenas que representan los subprotocolos.
     * @author Natalia González Álvarez
     */
    override fun getSubProtocols(): MutableList<String?> {
        return mutableListOf("subprotocol.demo.websocket")
    }
}