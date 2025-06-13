package org.example.prestamoordenadores.config.websockets

import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.lighthousegames.logging.logging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

private val log = logging()

/**
 * Controlador REST para la gestión de notificaciones de usuario a través de WebSockets.
 *
 * Este controlador expone endpoints para que los usuarios puedan obtener sus notificaciones,
 * marcarlas como leídas (individualmente o todas) y eliminarlas. Requiere autenticación
 * y autorización para acceder a sus funcionalidades, permitiendo a roles ADMIN, PROFESOR y ALUMNO.
 *
 * @property notificacionService El servicio encargado de la lógica de negocio de las notificaciones.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/notificaciones")
@PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO')")
class WebSocketController(
    private val notificacionService: WebSocketService
) {

    /**
     * Obtiene todas las notificaciones para el usuario autenticado.
     *
     * @param userDetails Los detalles del usuario autenticado, inyectados por Spring Security.
     * @return [ResponseEntity] con una lista de [NotificationDto] si la operación es exitosa,
     * o un estado 401 (Unauthorized) si no hay usuario autenticado.
     * @author Natalia González Álvarez
     */
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

    /**
     * Marca una notificación específica como leída para el usuario autenticado.
     *
     * @param userDetails Los detalles del usuario autenticado.
     * @param notificationId El ID de la notificación a marcar como leída.
     * @return [ResponseEntity] con la [NotificationDto] actualizada si se encuentra y se marca,
     * 401 (Unauthorized) si no hay usuario autenticado, o 404 (Not Found) si la notificación no existe
     * o no pertenece al usuario.
     * @author Natalia González Álvarez
     */
    @PostMapping("/{notificationId}/read")
    fun markAsRead(
        @AuthenticationPrincipal userDetails: UserDetails?,
        @PathVariable notificationId: String
    ): ResponseEntity<NotificationDto> {
        if (userDetails == null) return ResponseEntity.status(401).build()
        val updatedNotification = notificacionService.markAsRead(userDetails.username, notificationId)
        return updatedNotification?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     *
     * @param userDetails Los detalles del usuario autenticado.
     * @return [ResponseEntity] con una lista de todas las [NotificationDto] actualizadas,
     * o 401 (Unauthorized) si no hay usuario autenticado.
     * @author Natalia González Álvarez
     */
    @PostMapping("/read-all")
    fun markAllAsRead(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<List<NotificationDto>> {
        if (userDetails == null) return ResponseEntity.status(401).build()
        val updatedNotifications = notificacionService.markAllAsRead(userDetails.username)
        return ResponseEntity.ok(updatedNotifications)
    }

    /**
     * Elimina una notificación específica para el usuario autenticado.
     *
     * @param userDetails Los detalles del usuario autenticado.
     * @param notificationId El ID de la notificación a eliminar.
     * @return [ResponseEntity] con un estado 204 (No Content) si la eliminación es exitosa,
     * 401 (Unauthorized) si no hay usuario autenticado, o 404 (Not Found) si la notificación no existe
     * o no pertenece al usuario.
     * @author Natalia González Álvarez
     */
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