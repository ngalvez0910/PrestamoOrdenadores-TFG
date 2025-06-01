package org.example.prestamoordenadores.config.websockets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

private val log = logging()

/**
 * Servicio para la gestión y envío de notificaciones a usuarios a través de WebSockets.
 *
 * Este servicio mantiene un registro de las notificaciones por usuario y se encarga de enviarlas
 * utilizando un [WebSocketSender] inyectado. También provee funcionalidades para
 * obtener, crear, marcar como leídas (individualmente o todas) y eliminar notificaciones.
 *
 * @property webSocketSender El [WebSocketSender] utilizado para enviar mensajes a través de WebSockets,
 * calificado como "webSocketGlobalHandler".
 * @author Natalia González Álvarez
 */
@Service
class WebSocketService(
    @Qualifier("webSocketGlobalHandler") private val webSocketSender: WebSocketSender
) {
    private val userNotifications = ConcurrentHashMap<String, MutableList<NotificationDto>>()
    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    /**
     * Obtiene una lista de notificaciones para un usuario específico.
     * Las notificaciones se ordenan primero por si no están leídas (no leídas primero),
     * y luego por fecha de forma descendente.
     *
     * @param username El nombre de usuario para el que se desean obtener las notificaciones.
     * @return Una lista de [NotificationDto] para el usuario especificado. Si no hay notificaciones, devuelve una lista vacía.
     * @author Natalia González Álvarez
     */
    fun getNotificationsForUser(username: String): List<NotificationDto> {
        return userNotifications.getOrDefault(username, mutableListOf())
            .sortedWith(compareByDescending< NotificationDto> { !it.leida }.thenByDescending { it.fecha })
    }

    /**
     * Crea una nueva notificación y la envía al usuario especificado a través de WebSocket.
     * La nueva notificación se añade al principio de la lista de notificaciones del usuario.
     *
     * @param username El nombre de usuario al que se enviará la notificación.
     * @param notificacion La [NotificationDto] a crear y enviar.
     * @author Natalia González Álvarez
     */
    fun createAndSendNotification(username: String, notificacion: NotificationDto) {
        val userNotifs = userNotifications.computeIfAbsent(username) { mutableListOf() }
        userNotifs.add(0, notificacion)

        log.info { "Nueva notificación para $username: ${notificacion.titulo}" }

        try {
            val notificationJson = objectMapper.writeValueAsString(notificacion)
            webSocketSender.sendMessageToUser(username, notificationJson)
        } catch (e: Exception) {
            log.error(e) { "Error al serializar o enviar notificación por WebSocket" }
        }
    }

    /**
     * Marca una notificación específica como leída para un usuario dado.
     *
     * @param username El nombre de usuario propietario de la notificación.
     * @param notificationId El ID de la notificación a marcar como leída.
     * @return La [NotificationDto] actualizada si se encontró y se marcó como leída, o `null` si no se encontró.
     * @author Natalia González Álvarez
     */
    fun markAsRead(username: String, notificationId: String): NotificationDto? {
        val notif = userNotifications[username]?.find { it.id == notificationId }
        notif?.let { it.leida = true }
        return notif
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     *
     * @param username El nombre de usuario cuyas notificaciones se marcarán como leídas.
     * @return Una lista de todas las [NotificationDto] del usuario, con su estado `leida` actualizado.
     * @author Natalia González Álvarez
     */
    fun markAllAsRead(username: String): List<NotificationDto> {
        val notifs = userNotifications.getOrDefault(username, mutableListOf())
        notifs.forEach { it.leida = true }
        return notifs
    }

    /**
     * Elimina una notificación específica de un usuario.
     *
     * @param username El nombre de usuario propietario de la notificación.
     * @param notificationId El ID de la notificación a eliminar.
     * @return `true` si la notificación fue encontrada y eliminada, `false` en caso contrario.
     * @author Natalia González Álvarez
     */
    fun deleteNotification(username: String, notificationId: String): Boolean {
        return userNotifications[username]?.removeIf { it.id == notificationId } ?: false
    }
}