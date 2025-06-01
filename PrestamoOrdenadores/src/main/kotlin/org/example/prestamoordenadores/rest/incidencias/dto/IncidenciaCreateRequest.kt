package org.example.prestamoordenadores.rest.incidencias.dto

import org.jetbrains.annotations.NotNull

/**
 * Clase de datos (Data Class) que representa la solicitud para crear una nueva incidencia.
 * Contiene los campos obligatorios para registrar una incidencia en el sistema.
 *
 * @property asunto El asunto o título de la incidencia. No puede estar vacío.
 * @property descripcion Una descripción detallada de la incidencia. No puede estar vacía.
 * @author Natalia González Álvarez
 */
data class IncidenciaCreateRequest (
    @NotNull("Asunto no puede estar vacio")
    val asunto : String,
    @NotNull("Descripcion no puede estar vacía")
    val descripcion : String,
)