package org.example.prestamoordenadores.config.websockets.models

import java.time.LocalDateTime

data class NotificationDto(
    val id: String,
    val titulo: String,
    val mensaje: String?,
    val fecha: LocalDateTime,
    var leida: Boolean,
    val tipo: NotificationTypeDto?,
    val enlace: String?,
    val severidadSugerida: NotificationSeverityDto? = NotificationSeverityDto.INFO
)

enum class NotificationTypeDto {
    INFO, PRESTAMO, INCIDENCIA, SISTEMA, ADVERTENCIA, ERROR, SANCION
}

enum class NotificationSeverityDto {
    SUCCESS, INFO, WARNING, ERROR
}