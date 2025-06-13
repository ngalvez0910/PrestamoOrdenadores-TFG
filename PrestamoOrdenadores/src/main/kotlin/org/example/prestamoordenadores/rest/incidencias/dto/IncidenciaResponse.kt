package org.example.prestamoordenadores.rest.incidencias.dto

import org.example.prestamoordenadores.rest.users.dto.UserResponse

/**
 * Clase de datos (Data Class) que representa la respuesta básica de una incidencia.
 * Proporciona una vista simplificada de una incidencia, adecuada para usuarios generales.
 *
 * @property guid El identificador único global (GUID) de la incidencia.
 * @property asunto El asunto o título de la incidencia.
 * @property descripcion La descripción detallada de la incidencia.
 * @property estadoIncidencia El estado actual de la incidencia (ej. "ABIERTA", "EN_PROCESO", "CERRADA").
 * @property user La respuesta del usuario asociado a la incidencia.
 * @property createdDate La fecha de creación de la incidencia en formato de cadena.
 * @author Natalia González Álvarez
 */
data class IncidenciaResponse(
    val guid : String,
    val asunto : String,
    val descripcion : String,
    val estadoIncidencia : String,
    val user : UserResponse,
    val createdDate : String
)