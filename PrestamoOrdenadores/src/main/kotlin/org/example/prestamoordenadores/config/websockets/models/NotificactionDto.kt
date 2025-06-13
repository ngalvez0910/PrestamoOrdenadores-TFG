package org.example.prestamoordenadores.config.websockets.models

import java.time.LocalDateTime

/**
 * Representa una notificación que puede ser enviada a través de WebSockets.
 *
 * Contiene información como el título, mensaje, tipo, severidad y si ha sido leída.
 *
 * @property id Identificador único de la notificación.
 * @property titulo Título de la notificación.
 * @property mensaje Contenido o cuerpo de la notificación. Puede ser `null`.
 * @property fecha Fecha y hora en la que se generó la notificación.
 * @property leida Indica si la notificación ya fue leída.
 * @property tipo Tipo de la notificación (por ejemplo, INFO, PRESTAMO, ERROR...).
 * @property enlace Enlace relacionado a la notificación, si lo hay. Puede ser `null`.
 * @property severidadSugerida Nivel de severidad recomendado para mostrar la notificación visualmente. Por defecto es INFO.
 * @property mostrarToast Indica si la notificación debe mostrarse en un toast.
 *
 * @author Natalia González Álvarez
 */
data class NotificationDto(
    val id: String,
    val titulo: String,
    val mensaje: String?,
    val fecha: LocalDateTime,
    var leida: Boolean,
    val tipo: NotificationTypeDto?,
    val enlace: String?,
    val severidadSugerida: NotificationSeverityDto? = NotificationSeverityDto.INFO,
    val mostrarToast: Boolean = true
)

/**
 * Tipos posibles de notificación que pueden recibirse en el sistema.
 *
 * - `INFO`: Información general.
 * - `PRESTAMO`: Notificaciones relacionadas con préstamos.
 * - `INCIDENCIA`: Notificaciones sobre incidencias reportadas.
 * - `SISTEMA`: Mensajes relacionados con el sistema.
 * - `ADVERTENCIA`: Alertas que requieren atención del usuario.
 * - `ERROR`: Notificaciones de errores.
 * - `SANCION`: Notificaciones por sanciones o penalizaciones.
 *
 * @author Natalia González Álvarez
 */
enum class NotificationTypeDto {
    INFO, PRESTAMO, INCIDENCIA, SISTEMA, ADVERTENCIA, ERROR, SANCION
}

/**
 * Severidades sugeridas para el estilo visual de la notificación.
 *
 * - `SUCCESS`: Operación exitosa.
 * - `INFO`: Información general.
 * - `WARNING`: Advertencia.
 * - `ERROR`: Error grave.
 *
 * Estas severidades pueden usarse para colorear o priorizar visualmente las notificaciones en el cliente.
 *
 * @author Natalia González Álvarez
 */
enum class NotificationSeverityDto {
    SUCCESS, INFO, WARNING, ERROR
}