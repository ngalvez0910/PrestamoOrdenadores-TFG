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
    fun getAllPrestamos(): Result<List<PrestamoResponse>, PrestamoError>
    fun getPrestamoByGuid(guid: String) : Result<PrestamoResponse?, PrestamoError>
    fun createPrestamo(prestamo: PrestamoCreateRequest) : Result<PrestamoResponse, PrestamoError>
    fun updatePrestamo(guid: String, prestamo: PrestamoUpdateRequest) : Result<PrestamoResponse?, PrestamoError>
    fun deletePrestamoByGuid(guid: String) : Result<PrestamoResponse?, PrestamoError>
    fun getByFechaPrestamo(fecha: LocalDate): Result<List<PrestamoResponse>, PrestamoError>
    fun getByFechaDevolucion(fecha: LocalDate): Result<List<PrestamoResponse>, PrestamoError>
    fun getPrestamoByUserGuid(userGuid: String) : Result<List<PrestamoResponse>, PrestamoError>
}