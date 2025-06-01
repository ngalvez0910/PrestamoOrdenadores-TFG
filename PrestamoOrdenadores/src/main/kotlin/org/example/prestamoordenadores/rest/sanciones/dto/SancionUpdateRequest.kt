package org.example.prestamoordenadores.rest.sanciones.dto

/**
 * Clase de datos (Data Class) que representa la solicitud para actualizar una sanción existente.
 * Actualmente, solo permite la actualización del tipo de sanción.
 *
 * @property tipoSancion El nuevo tipo de sanción.
 * @author Natalia González Álvarez
 */
data class SancionUpdateRequest(
    val tipoSancion : String
)