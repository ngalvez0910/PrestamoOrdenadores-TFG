package org.example.prestamoordenadores.rest.sanciones.dto

import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponse

/**
 * Clase de datos (Data Class) que representa la respuesta básica de una sanción.
 * Proporciona una vista simplificada de una sanción, adecuada para usuarios generales.
 *
 * @property guid El identificador único global (GUID) de la sanción.
 * @property user La respuesta del usuario sancionado.
 * @property prestamo La respuesta del préstamo asociado a la sanción.
 * @property tipoSancion El tipo de sanción (ej. "RETRAZO", "DAÑO").
 * @property fechaSancion La fecha en que se aplicó la sanción en formato de cadena.
 * @property fechaFin La fecha de finalización de la sanción en formato de cadena.
 * @author Natalia González Álvarez
 */
data class SancionResponse(
    val guid : String,
    val user : UserResponse,
    val prestamo: PrestamoResponse,
    val tipoSancion : String,
    val fechaSancion : String,
    val fechaFin : String
)