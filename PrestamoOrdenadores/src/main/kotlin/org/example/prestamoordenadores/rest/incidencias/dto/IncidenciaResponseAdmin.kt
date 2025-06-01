package org.example.prestamoordenadores.rest.incidencias.dto

import org.example.prestamoordenadores.rest.users.dto.UserResponse

/**
 * Clase de datos (Data Class) que representa la respuesta detallada de una incidencia para administradores.
 * Incluye información adicional como fechas de actualización y el estado de eliminación lógica,
 * lo que la hace adecuada para vistas administrativas.
 *
 * @property guid El identificador único global (GUID) de la incidencia.
 * @property asunto El asunto o título de la incidencia.
 * @property descripcion La descripción detallada de la incidencia.
 * @property estadoIncidencia El estado actual de la incidencia (ej. "ABIERTA", "EN_PROCESO", "CERRADA").
 * @property user La respuesta del usuario asociado a la incidencia.
 * @property createdDate La fecha de creación de la incidencia en formato de cadena.
 * @property updatedDate La fecha de la última actualización de la incidencia en formato de cadena.
 * @property isDeleted Indica si la incidencia ha sido marcada para eliminación lógica.
 * @author Natalia González Álvarez
 */
data class IncidenciaResponseAdmin(
    val guid : String,
    val asunto : String,
    val descripcion : String,
    val estadoIncidencia : String,
    val user : UserResponse,
    val createdDate : String,
    val updatedDate : String,
    val isDeleted: Boolean
)