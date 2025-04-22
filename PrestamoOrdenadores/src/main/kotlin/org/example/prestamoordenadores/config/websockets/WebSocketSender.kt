package org.example.prestamoordenadores.config.websockets

import java.io.IOException

interface WebSocketSender {
    @Throws(IOException::class)
    fun sendMessage(message: String?)

    @Throws(IOException::class)
    fun sendMessageToUser(username: String?, message: String?)

    /**
     * Envía mensajes periódicos a todos los clientes conectados en un intervalo de tiempo regular.
     *
     *
     *
     * Este método está diseñado para enviar mensajes automáticamente en intervalos programados a través
     * de la lógica de envío periódica, como notificaciones automáticas.
     *
     *
     * @throws IOException En caso de error en la transmisión de los mensajes periódicos.
     *
    @Throws(IOException::class)
    fun sendPeriodicMessages()
     */
}