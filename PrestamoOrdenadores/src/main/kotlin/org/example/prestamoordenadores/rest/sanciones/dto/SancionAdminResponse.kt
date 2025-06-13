package org.example.prestamoordenadores.rest.sanciones.dto

import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponse

/**
 * Clase de datos (Data Class) que representa la respuesta detallada de una sanción para administradores.
 * Incluye información adicional como fechas de creación y actualización, y el estado de eliminación lógica,
 * lo que la hace adecuada para vistas administrativas.
 *
 * @property guid El identificador único global (GUID) de la sanción.
 * @property user La respuesta del usuario sancionado.
 * @property prestamo La respuesta del préstamo asociado a la sanción.
 * @property tipoSancion El tipo de sanción (ej. "RETRAZO", "DAÑO").
 * @property fechaSancion La fecha en que se aplicó la sanción en formato de cadena.
 * @property fechaFin La fecha de finalización de la sanción en formato de cadena.
 * @property createdDate La fecha de creación de la sanción en formato de cadena.
 * @property updatedDate La fecha de la última actualización de la sanción en formato de cadena.
 * @property isDeleted Indica si la sanción ha sido marcada para eliminación lógica.
 * @author Natalia González Álvarez
 */
data class SancionAdminResponse(
    val guid : String,
    val user : UserResponse,
    val prestamo: PrestamoResponse,
    val tipoSancion : String,
    val fechaSancion : String,
    val fechaFin : String,
    var createdDate: String,
    var updatedDate: String,
    var isDeleted: Boolean
)