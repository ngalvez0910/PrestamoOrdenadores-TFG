package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
interface PrestamoService {
    fun getAllPrestamos(page: Int, size: Int): Result<PagedResponse<PrestamoResponse>, PrestamoError>
    fun getPrestamoByGuid(guid: String) : Result<PrestamoResponseAdmin?, PrestamoError>
    fun createPrestamo() : Result<PrestamoResponse, PrestamoError>
    fun updatePrestamo(guid: String, prestamo: PrestamoUpdateRequest) : Result<PrestamoResponse?, PrestamoError>
    fun deletePrestamoByGuid(guid: String) : Result<PrestamoResponseAdmin, PrestamoError>
    fun getByFechaPrestamo(fechaPrestamo: LocalDate): Result<List<PrestamoResponse>, PrestamoError>
    fun getByFechaDevolucion(fechaDevolucion: LocalDate): Result<List<PrestamoResponse>, PrestamoError>
    fun getPrestamoByUserGuid(userGuid: String) : Result<List<PrestamoResponse>, PrestamoError>
    fun cancelarPrestamo(guid: String) : Result<PrestamoResponse?, PrestamoError>
}