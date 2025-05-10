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

class WebSocketHandler(
    private val entity: String?
) : TextWebSocketHandler(), SubProtocolCapable, WebSocketSender {
    private val sessions: MutableSet<WebSocketSession> = CopyOnWriteArraySet()

    private val userSessionsMap: MutableMap<String?, WebSocketSession?> =
        ConcurrentHashMap<String?, WebSocketSession?>()

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
     * Envía mensajes periódicos a todas las sesiones activas cada segundo.

    @Scheduled(fixedRate = 1000)
    @Throws(IOException::class)
    fun sendPeriodicMessages() {
        for (session in sessions) {
            if (session.isOpen()) {
                val broadcast = "Mensaje periódico del servidor: " + LocalTime.now()
                session.sendMessage(TextMessage(broadcast))
                log.info("Mensaje periódico enviado: {}", broadcast)
            }
        }
    }*/

    @Throws(
        Exception::class
    )
    protected override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
    }

    @Throws(
        Exception::class
    )
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    }

    override fun getSubProtocols(): MutableList<String?> {
        return mutableListOf("subprotocol.demo.websocket")
    }
}