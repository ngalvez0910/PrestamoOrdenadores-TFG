package org.example.prestamoordenadores.rest.prestamos.dto

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponse

/**
 * Clase de datos (Data Class) que representa la respuesta básica de un préstamo.
 * Proporciona una vista simplificada de un préstamo, adecuada para usuarios generales.
 *
 * @property guid El identificador único global (GUID) del préstamo.
 * @property user La respuesta del usuario asociado al préstamo.
 * @property dispositivo La respuesta del dispositivo asociado al préstamo.
 * @property estadoPrestamo El estado actual del préstamo (ej. "ACTIVO", "FINALIZADO").
 * @property fechaPrestamo La fecha de inicio del préstamo en formato de cadena.
 * @property fechaDevolucion La fecha de devolución del préstamo en formato de cadena, puede ser nula si aún no se ha devuelto.
 * @author Natalia González Álvarez
 */
data class PrestamoResponse (
    var guid: String,
    var user: UserResponse,
    var dispositivo: DispositivoResponse,
    var estadoPrestamo: String,
    var fechaPrestamo: String,
    var fechaDevolucion: String?
)