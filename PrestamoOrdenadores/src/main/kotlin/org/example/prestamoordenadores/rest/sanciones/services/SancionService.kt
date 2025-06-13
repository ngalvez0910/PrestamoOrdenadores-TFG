package org.example.prestamoordenadores.rest.sanciones.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.sanciones.dto.SancionAdminResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Interfaz para el servicio de gestión de sanciones.
 *
 * Define las operaciones de negocio relacionadas con las sanciones, incluyendo
 * la obtención, actualización y eliminación de sanciones, así como
 * consultas por fecha, tipo o por usuario.
 * Utiliza el tipo `Result` para manejar operaciones que pueden fallar, encapsulando
 * el éxito con el tipo de respuesta esperado o un [SancionError] en caso de fallo.
 *
 * @author Natalia González Álvarez
 */
@Service
interface SancionService {
    /**
     * Obtiene una lista paginada de todas las sanciones.
     *
     * @param page El número de página a recuperar (base 0).
     * @param size El tamaño de la página.
     * @return Un [Result] que contiene un [PagedResponse] de [SancionResponse] si es exitoso,
     * o un [SancionError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getAllSanciones(page: Int, size: Int): Result<PagedResponse<SancionResponse>, SancionError>

    /**
     * Obtiene una sanción específica por su identificador único global (GUID) para una vista de usuario normal.
     *
     * @param guid El GUID de la sanción a buscar.
     * @return Un [Result] que contiene un [SancionResponse] (o `null` si no se encuentra) si es exitoso,
     * o un [SancionError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getSancionByGuid(guid: String): Result<SancionResponse?, SancionError>

    /**
     * Obtiene una sanción específica por su identificador único global (GUID) para una vista administrativa.
     *
     * @param guid El GUID de la sanción a buscar.
     * @return Un [Result] que contiene un [SancionAdminResponse] (o `null` si no se encuentra) si es exitoso,
     * o un [SancionError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getSancionByGuidAdmin(guid: String): Result<SancionAdminResponse?, SancionError>

    /**
     * Actualiza una sanción existente identificada por su GUID.
     *
     * @param guid El GUID de la sanción a actualizar.
     * @param sancion La solicitud [SancionUpdateRequest] con los datos a actualizar.
     * @return Un [Result] que contiene un [SancionResponse] (o `null` si no se encuentra) de la sanción actualizada si es exitoso,
     * o un [SancionError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun updateSancion(guid: String, sancion: SancionUpdateRequest): Result<SancionResponse?, SancionError>

    /**
     * Elimina lógicamente una sanción por su GUID, marcándola como eliminada.
     *
     * @param guid El GUID de la sanción a eliminar lógicamente.
     * @return Un [Result] que contiene un [SancionAdminResponse] de la sanción eliminada si es exitoso,
     * o un [SancionError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun deleteSancionByGuid(guid: String): Result<SancionAdminResponse?, SancionError>

    /**
     * Obtiene una lista de sanciones por su fecha de aplicación.
     *
     * @param fecha La fecha de las sanciones a buscar.
     * @return Un [Result] que contiene una lista de [SancionResponse] si es exitoso,
     * o un [SancionError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getByFecha(fecha: LocalDate): Result<List<SancionResponse>, SancionError>

    /**
     * Obtiene una lista de sanciones por su tipo.
     *
     * @param tipo El tipo de sanción a buscar (como String).
     * @return Un [Result] que contiene una lista de [SancionResponse] si es exitoso,
     * o un [SancionError] si ocurre un problema (ej. tipo de sanción inválido).
     * @author Natalia González Álvarez
     */
    fun getByTipo(tipo: String): Result<List<SancionResponse>, SancionError>

    /**
     * Obtiene una lista de sanciones asociadas a un usuario específico por su GUID.
     *
     * @param userGuid El GUID del usuario cuyas sanciones se desean buscar.
     * @return Un [Result] que contiene una lista de [SancionResponse] si es exitoso,
     * o un [SancionError] si ocurre un problema (ej. usuario no encontrado).
     * @author Natalia González Álvarez
     */
    fun getSancionByUserGuid(userGuid: String): Result<List<SancionResponse>, SancionError>
}