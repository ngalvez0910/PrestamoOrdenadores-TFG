package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PrestamoServiceImpl(
    private val prestamoRepository: PrestamoRepository
): PrestamoService {
    override fun getAllPrestamos(): Result<List<PrestamoResponse>, PrestamoError> {
        TODO("Not yet implemented")
    }

    override fun getPrestamoByGuid(guid: String): Result<PrestamoResponse, PrestamoError> {
        TODO("Not yet implemented")
    }

    override fun createPrestamo(prestamo: PrestamoCreateRequest): Result<PrestamoResponse, PrestamoError> {
        TODO("Not yet implemented")
    }

    override fun updatePrestamo(prestamo: PrestamoUpdateRequest): Result<PrestamoResponse, PrestamoError> {
        TODO("Not yet implemented")
    }

    override fun deletePrestamoByGuid(guid: String): Result<PrestamoResponse, PrestamoError> {
        TODO("Not yet implemented")
    }

    override fun getByFechaPrestamo(fecha: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        TODO("Not yet implemented")
    }

    override fun getByFechaDevolucion(fecha: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        TODO("Not yet implemented")
    }

    override fun getPrestamoByUserGuid(userGuid: String): Result<List<PrestamoResponse>, PrestamoError> {
        TODO("Not yet implemented")
    }
}