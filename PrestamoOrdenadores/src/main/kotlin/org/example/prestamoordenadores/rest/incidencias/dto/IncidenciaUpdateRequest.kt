package org.example.prestamoordenadores.rest.incidencias.dto

import org.jetbrains.annotations.NotNull

/**
 * Clase de datos (Data Class) que representa la solicitud para actualizar el estado de una incidencia.
 *
 * Actualmente, solo permite la actualización del estado de la incidencia.
 *
 * @property estadoIncidencia El nuevo estado de la incidencia. No puede estar vacío.
 * @author Natalia González Álvarez
 */
data class IncidenciaUpdateRequest (
    @NotNull("Estado de la incidencia no puede estar vacío")
    val estadoIncidencia : String,
)