package org.example.prestamoordenadores.config.websockets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

private val log = logging()

@Service
class WebSocketService(
    @Qualifier("webSocketGlobalHandler") private val webSocketSender: WebSocketSender
) {
    private val userNotifications = ConcurrentHashMap<String, MutableList<NotificationDto>>()
    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())


    fun getNotificationsForUser(username: String): List<NotificationDto> {
        return userNotifications.getOrDefault(username, mutableListOf())
            .sortedWith(compareByDescending< NotificationDto> { !it.leida }.thenByDescending { it.fecha })
    }

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

    fun markAsRead(username: String, notificationId: String): NotificationDto? {
        val notif = userNotifications[username]?.find { it.id == notificationId }
        notif?.let { it.leida = true }
        return notif
    }

    fun markAllAsRead(username: String): List<NotificationDto> {
        val notifs = userNotifications.getOrDefault(username, mutableListOf())
        notifs.forEach { it.leida = true }
        return notifs
    }

    fun deleteNotification(username: String, notificationId: String): Boolean {
        return userNotifications[username]?.removeIf { it.id == notificationId } ?: false
    }
}