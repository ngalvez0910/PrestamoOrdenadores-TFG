package org.example.prestamoordenadores.rest.prestamos.dto

import org.jetbrains.annotations.NotNull

/**
 * Clase de datos (Data Class) que representa la solicitud para actualizar un préstamo existente.
 * Actualmente, solo permite la actualización del estado del préstamo.
 *
 * @property estadoPrestamo El nuevo estado del préstamo. No puede ser nulo.
 * @author Natalia González Álvarez
 */
data class PrestamoUpdateRequest(
    @NotNull("Estado no puede ser null")
    val estadoPrestamo: String,
)