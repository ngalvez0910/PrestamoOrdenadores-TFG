package org.example.prestamoordenadores.rest.incidencias.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponseAdmin
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service

/**
 * Interfaz para el servicio de gestión de incidencias.
 *
 * Define las operaciones de negocio relacionadas con las incidencias, incluyendo
 * la obtención, creación, actualización y eliminación de incidencias, así como
 * consultas por estado o por usuario.
 * Utiliza el tipo `Result` para manejar operaciones que pueden fallar, encapsulando
 * el éxito con el tipo de respuesta esperado o un [IncidenciaError] en caso de fallo.
 *
 * @author Natalia González Álvarez
 */
@Service
interface IncidenciaService {
    /**
     * Obtiene una lista paginada de todas las incidencias.
     *
     * @param page El número de página a recuperar (base 0).
     * @param size El tamaño de la página.
     * @return Un [Result] que contiene un [PagedResponse] de [IncidenciaResponse] si es exitoso,
     * o un [IncidenciaError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getAllIncidencias(page: Int, size: Int): Result<PagedResponse<IncidenciaResponse>, IncidenciaError>

    /**
     * Obtiene una incidencia específica por su identificador único global (GUID) para una vista de usuario.
     *
     * @param guid El GUID de la incidencia a buscar.
     * @return Un [Result] que contiene un [IncidenciaResponse] (o `null` si no se encuentra) si es exitoso,
     * o un [IncidenciaError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getIncidenciaByGuid(guid: String) : Result<IncidenciaResponse?, IncidenciaError>

    /**
     * Obtiene una incidencia específica por su identificador único global (GUID) para una vista administrativa.
     *
     * @param guid El GUID de la incidencia a buscar.
     * @return Un [Result] que contiene un [IncidenciaResponseAdmin] (o `null` si no se encuentra) si es exitoso,
     * o un [IncidenciaError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getIncidenciaByGuidAdmin(guid: String) : Result<IncidenciaResponseAdmin?, IncidenciaError>

    /**
     * Crea una nueva incidencia.
     *
     * @param incidencia El objeto [IncidenciaCreateRequest] con los datos de la nueva incidencia.
     * @return Un [Result] que contiene un [IncidenciaResponse] de la incidencia creada si es exitoso,
     * o un [IncidenciaError] si ocurre un problema (ej. validación, usuario no encontrado).
     * @author Natalia González Álvarez
     */
    fun createIncidencia(incidencia: IncidenciaCreateRequest) : Result<IncidenciaResponse, IncidenciaError>

    /**
     * Actualiza una incidencia existente identificada por su GUID.
     *
     * @param guid El GUID de la incidencia a actualizar.
     * @param incidencia El objeto [IncidenciaUpdateRequest] con los datos a actualizar (actualmente solo el estado).
     * @return Un [Result] que contiene un [IncidenciaResponse] (o `null` si no se encuentra) de la incidencia actualizada si es exitoso,
     * o un [IncidenciaError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun updateIncidencia(guid: String, incidencia: IncidenciaUpdateRequest) : Result<IncidenciaResponse?, IncidenciaError>

    /**
     * Elimina lógicamente una incidencia por su GUID, marcándola como eliminada.
     *
     * @param guid El GUID de la incidencia a eliminar lógicamente.
     * @return Un [Result] que contiene un [IncidenciaResponseAdmin] (o `null` si no se encuentra) de la incidencia eliminada si es exitoso,
     * o un [IncidenciaError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun deleteIncidenciaByGuid(guid: String) : Result<IncidenciaResponseAdmin?, IncidenciaError>

    /**
     * Obtiene una lista de incidencias por su estado.
     *
     * @param estado La cadena de texto que representa el estado de la incidencia (ej. "PENDIENTE", "RESUELTO").
     * @return Un [Result] que contiene una lista de [IncidenciaResponse] si es exitoso,
     * o un [IncidenciaError] si ocurre un problema (ej. estado inválido).
     * @author Natalia González Álvarez
     */
    fun getIncidenciaByEstado(estado: String): Result<List<IncidenciaResponse>, IncidenciaError>

    /**
     * Obtiene una lista de incidencias asociadas a un usuario específico por su GUID.
     *
     * @param userGuid El GUID del usuario cuyas incidencias se desean buscar.
     * @return Un [Result] que contiene una lista de [IncidenciaResponse] si es exitoso,
     * o un [IncidenciaError] si ocurre un problema (ej. usuario no encontrado).
     * @author Natalia González Álvarez
     */
    fun getIncidenciasByUserGuid(userGuid: String) : Result<List<IncidenciaResponse>, IncidenciaError>
}