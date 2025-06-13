package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Interfaz para el servicio de gestión de préstamos.
 *
 * Define las operaciones de negocio relacionadas con los préstamos, incluyendo
 * la obtención, creación, actualización y eliminación de préstamos, así como
 * consultas por fecha o por usuario.
 * Utiliza el tipo `Result` para manejar operaciones que pueden fallar, encapsulando
 * el éxito con el tipo de respuesta esperado o un [PrestamoError] en caso de fallo.
 *
 * @author Natalia González Álvarez
 */
@Service
interface PrestamoService {
    /**
     * Obtiene una lista paginada de todos los préstamos.
     *
     * @param page El número de página a recuperar (base 0).
     * @param size El tamaño de la página.
     * @return Un [Result] que contiene un [PagedResponse] de [PrestamoResponse] si es exitoso,
     * o un [PrestamoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getAllPrestamos(page: Int, size: Int): Result<PagedResponse<PrestamoResponse>, PrestamoError>

    /**
     * Obtiene un préstamo específico por su identificador único global (GUID).
     *
     * @param guid El GUID del préstamo a buscar.
     * @return Un [Result] que contiene un [PrestamoResponseAdmin] (o `null` si no se encuentra) si es exitoso,
     * o un [PrestamoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getPrestamoByGuid(guid: String) : Result<PrestamoResponseAdmin?, PrestamoError>

    /**
     * Crea un nuevo préstamo.
     *
     * @return Un [Result] que contiene un [PrestamoResponse] del préstamo creado si es exitoso,
     * o un [PrestamoError] si ocurre un problema (ej. usuario no encontrado, dispositivo no disponible).
     * @author Natalia González Álvarez
     */
    fun createPrestamo() : Result<PrestamoResponse, PrestamoError>

    /**
     * Actualiza un préstamo existente identificado por su GUID.
     *
     * @param guid El GUID del préstamo a actualizar.
     * @param prestamo El objeto [PrestamoUpdateRequest] con los datos a actualizar (actualmente solo el estado).
     * @return Un [Result] que contiene un [PrestamoResponse] (o `null` si no se encuentra) del préstamo actualizado si es exitoso,
     * o un [PrestamoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun updatePrestamo(guid: String, prestamo: PrestamoUpdateRequest) : Result<PrestamoResponse?, PrestamoError>

    /**
     * Elimina lógicamente un préstamo por su GUID, marcándolo como eliminado.
     *
     * @param guid El GUID del préstamo a eliminar lógicamente.
     * @return Un [Result] que contiene un [PrestamoResponseAdmin] del préstamo eliminado si es exitoso,
     * o un [PrestamoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun deletePrestamoByGuid(guid: String) : Result<PrestamoResponseAdmin, PrestamoError>

    /**
     * Obtiene una lista de préstamos por su fecha de inicio.
     *
     * @param fechaPrestamo La fecha de inicio de los préstamos a buscar.
     * @return Un [Result] que contiene una lista de [PrestamoResponse] si es exitoso,
     * o un [PrestamoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getByFechaPrestamo(fechaPrestamo: LocalDate): Result<List<PrestamoResponse>, PrestamoError>

    /**
     * Obtiene una lista de préstamos por su fecha de devolución.
     *
     * @param fechaDevolucion La fecha de devolución de los préstamos a buscar.
     * @return Un [Result] que contiene una lista de [PrestamoResponse] si es exitoso,
     * o un [PrestamoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun getByFechaDevolucion(fechaDevolucion: LocalDate): Result<List<PrestamoResponse>, PrestamoError>

    /**
     * Obtiene una lista de préstamos asociados a un usuario específico por su GUID.
     *
     * @param userGuid El GUID del usuario cuyas préstamos se desean buscar.
     * @return Un [Result] que contiene una lista de [PrestamoResponse] si es exitoso,
     * o un [PrestamoError] si ocurre un problema (ej. usuario no encontrado).
     * @author Natalia González Álvarez
     */
    fun getPrestamoByUserGuid(userGuid: String) : Result<List<PrestamoResponse>, PrestamoError>

    /**
     * Cancela un préstamo específico por su GUID.
     *
     * @param guid El GUID del préstamo a cancelar.
     * @return Un [Result] que contiene un [PrestamoResponse] del préstamo cancelado si es exitoso,
     * o un [PrestamoError] si ocurre un problema.
     * @author Natalia González Álvarez
     */
    fun cancelarPrestamo(guid: String) : Result<PrestamoResponse?, PrestamoError>
}