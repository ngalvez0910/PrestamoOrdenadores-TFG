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

    @Throws(
        Exception::class
    )
    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info{ "Conexión establecida con el servidor: $session" }
        log.info { "Sesión: $session" }

        // obtenemos el nombre del usuario autenticado y asociamos la sesión de WebSocket
        // con el nombre de usuario en userSessionsMap
        //String username = getUsername();

        val username = session.attributes.get("username")

        if (username != null) {
            userSessionsMap.put(username as String?, session)
            log.info { "Usuario: $username añadido a mapa de sesiones" }
        }

        sessions.add(session)
        val message = TextMessage("Web socket: $entity - LoanTech")
        log.info { "Servidor envía: $message" }
        session.sendMessage(message)
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info { "Cerrando la conexión con el servidor: $status" }

        val username = session.attributes["username"] as? String

        if (username != null) {
            userSessionsMap.remove(username)
            log.info { "Usuario: $username eliminado del mapa de sesiones" }
        }

        sessions.remove(session)
        log.info { "Conexión cerrada con el servidor: $status" }
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
        log.info { "Enviar mensaje de cambios en la entidad: $entity a usuario: $username : $message" }

        val session: WebSocketSession? = userSessionsMap.get(username)

        if (session != null && session.isOpen) {
            message?.let { session.sendMessage(TextMessage(it)) }
            log.info { "Servidor WS envía a $username : $message" }
        } else {
            log.info { "Usuario: $username no conectado, no se le envió cambios en la entidad: $entity" }
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
        // No hacer nada con los mensajes recibidos
    }

    @Throws(
        Exception::class
    )
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        // No hacer nada con los mensajes recibidos
    }

    override fun getSubProtocols(): MutableList<String?> {
        return mutableListOf("subprotocol.demo.websocket")
    }
}