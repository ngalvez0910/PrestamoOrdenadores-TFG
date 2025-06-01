package org.example.prestamoordenadores.config.websockets

import java.io.IOException

/**
 * Interfaz para definir el contrato de envío de mensajes a través de WebSockets.
 *
 * Proporciona métodos para enviar mensajes a todas las sesiones conectadas
 * y para enviar mensajes a un usuario específico.
 *
 * @author Natalia González Álvarez
 */
interface WebSocketSender {

    /**
     * Envía un mensaje a todas las sesiones WebSocket conectadas.
     *
     * @param message El mensaje de texto a enviar. Puede ser nulo.
     * @throws IOException Si ocurre un error de entrada/salida al enviar el mensaje.
     * @author Natalia González Álvarez
     */
    @Throws(IOException::class)
    fun sendMessage(message: String?)

    /**
     * Envía un mensaje a una sesión WebSocket específica asociada a un nombre de usuario.
     *
     * @param username El nombre de usuario del destinatario. Puede ser nulo.
     * @param message El mensaje de texto a enviar. Puede ser nulo.
     * @throws IOException Si ocurre un error de entrada/salida al enviar el mensaje.
     * @author Natalia González Álvarez
     */
    @Throws(IOException::class)
    fun sendMessageToUser(username: String?, message: String?)
}