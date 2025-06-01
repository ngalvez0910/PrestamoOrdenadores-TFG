package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service

/**
 * Interfaz para el servicio de gestión de dispositivos.
 *
 * Define las operaciones de negocio relacionadas con los dispositivos, incluyendo
 * la obtención, creación, actualización y eliminación de dispositivos, así como
 * consultas de stock y por atributos específicos.
 * Utiliza el tipo `Result` para manejar operaciones que pueden fallar, encapsulando
 * el éxito con el tipo de respuesta esperado o un [DispositivoError] en caso de fallo.
 *
 * @author Natalia González Álvarez
 */
@Service
interface DispositivoService {
    /**
     * Obtiene una lista paginada de todos los dispositivos, con detalles administrativos.
     *
     * @param page El número de página a recuperar (base 0).
     * @param size El tamaño de la página.
     * @return Un [Result] que contiene un [PagedResponse] de [DispositivoResponseAdmin] si es exitoso,
     * o un [DispositivoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getAllDispositivos(page: Int, size: Int): Result<PagedResponse<DispositivoResponseAdmin>, DispositivoError>

    /**
     * Obtiene un dispositivo específico por su identificador único global (GUID).
     *
     * @param guid El GUID del dispositivo a buscar.
     * @return Un [Result] que contiene un [DispositivoResponseAdmin] (o `null` si no se encuentra) si es exitoso,
     * o un [DispositivoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getDispositivoByGuid(guid: String): Result<DispositivoResponseAdmin?, DispositivoError>

    /**
     * Crea un nuevo dispositivo.
     *
     * @param dispositivo El objeto [DispositivoCreateRequest] con los datos del nuevo dispositivo.
     * @return Un [Result] que contiene un [DispositivoResponse] del dispositivo creado si es exitoso,
     * o un [DispositivoError] si ocurre un problema (ej. validación).
     * @author Natalia González Álvarez
     */
    fun createDispositivo(dispositivo: DispositivoCreateRequest): Result<DispositivoResponse, DispositivoError>

    /**
     * Actualiza un dispositivo existente identificado por su GUID.
     *
     * @param guid El GUID del dispositivo a actualizar.
     * @param dispositivo El objeto [DispositivoUpdateRequest] con los datos a actualizar.
     * @return Un [Result] que contiene un [DispositivoResponseAdmin] (o `null` si no se encuentra) si es exitoso,
     * o un [DispositivoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest): Result<DispositivoResponseAdmin?, DispositivoError>

    /**
     * Elimina lógicamente un dispositivo por su GUID, marcándolo como no disponible.
     *
     * @param guid El GUID del dispositivo a eliminar lógicamente.
     * @return Un [Result] que contiene un [DispositivoResponseAdmin] (o `null` si no se encuentra) del dispositivo eliminado si es exitoso,
     * o un [DispositivoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun deleteDispositivoByGuid(guid: String): Result<DispositivoResponseAdmin?, DispositivoError>

    /**
     * Obtiene un dispositivo por su número de serie.
     *
     * @param numeroSerie El número de serie del dispositivo a buscar.
     * @return Un [Result] que contiene un [DispositivoResponseAdmin] (o `null` si no se encuentra) si es exitoso,
     * o un [DispositivoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getDispositivoByNumeroSerie(numeroSerie: String): Result<DispositivoResponseAdmin?, DispositivoError>

    /**
     * Obtiene una lista de dispositivos por su estado.
     *
     * @param estado La cadena de texto que representa el estado del dispositivo (ej. "DISPONIBLE", "PRESTADO").
     * @return Un [Result] que contiene una lista de [DispositivoResponseAdmin] si es exitoso,
     * o un [DispositivoError] si ocurre un problema (ej. estado inválido).
     * @author Natalia González Álvarez
     */
    fun getDispositivoByEstado(estado: String): Result<List<DispositivoResponseAdmin>, DispositivoError>

    /**
     * Obtiene el número total de dispositivos disponibles en stock.
     *
     * @return Un [Result] que contiene un [Int] con el número de dispositivos disponibles si es exitoso,
     * o un [DispositivoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getStock(): Result<Int, DispositivoError>
}