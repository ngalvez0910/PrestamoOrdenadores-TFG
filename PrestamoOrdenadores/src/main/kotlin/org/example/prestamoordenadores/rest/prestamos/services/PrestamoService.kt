package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
interface PrestamoService {
    suspend fun getAllPrestamos(): Result<List<PrestamoResponse>, PrestamoError>
    suspend fun getPrestamoByGuid(guid: String) : Result<PrestamoResponse?, PrestamoError>
    suspend fun createPrestamo(prestamo: PrestamoCreateRequest) : Result<PrestamoResponse, PrestamoError>
    suspend fun updatePrestamo(guid: String, prestamo: PrestamoUpdateRequest) : Result<PrestamoResponse?, PrestamoError>
    suspend fun deletePrestamoByGuid(guid: String) : Result<PrestamoResponse?, PrestamoError>
    suspend fun getByFechaPrestamo(fechaPrestamo: LocalDate): Result<List<PrestamoResponse>, PrestamoError>
    suspend fun getByFechaDevolucion(fechaDevolucion: LocalDate): Result<List<PrestamoResponse>, PrestamoError>
    suspend fun getPrestamoByUserGuid(userGuid: String) : Result<List<PrestamoResponse>, PrestamoError>
}