package org.example.prestamoordenadores.config.websockets

import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.lighthousegames.logging.logging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

private val log = logging()

@RestController
@RequestMapping("/notificaciones")
@PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO')")
class WebSocketController(
    private val notificacionService: WebSocketService
) {

    @GetMapping("")
    fun getMyNotifications(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<List<NotificationDto>> {
        if (userDetails == null) {
            log.warn { "Intento de acceso a notificaciones sin autenticación." }
            return ResponseEntity.status(401).build() // Unauthorized
        }
        val username = userDetails.username
        log.info { "Obteniendo notificaciones para el usuario: $username" }
        val notifications = notificacionService.getNotificationsForUser(username)
        return ResponseEntity.ok(notifications)
    }

    @PostMapping("/{notificationId}/read")
    fun markAsRead(
        @AuthenticationPrincipal userDetails: UserDetails?,
        @PathVariable notificationId: String
    ): ResponseEntity<NotificationDto> {
        if (userDetails == null) return ResponseEntity.status(401).build()
        val updatedNotification = notificacionService.markAsRead(userDetails.username, notificationId)
        return updatedNotification?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/read-all")
    fun markAllAsRead(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<List<NotificationDto>> {
        if (userDetails == null) return ResponseEntity.status(401).build()
        val updatedNotifications = notificacionService.markAllAsRead(userDetails.username)
        return ResponseEntity.ok(updatedNotifications)
    }

    @DeleteMapping("/{notificationId}")
    fun deleteNotification(
        @AuthenticationPrincipal userDetails: UserDetails?,
        @PathVariable notificationId: String
    ): ResponseEntity<Unit> {
        if (userDetails == null) return ResponseEntity.status(401).build()
        return if (notificacionService.deleteNotification(userDetails.username, notificationId)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}