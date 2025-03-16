package org.example.prestamoordenadores.rest.sanciones.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
interface SancionService {
    suspend fun getAllSanciones(): Result<List<SancionResponse>, SancionError>
    suspend fun getSancionByGuid(guid: String) : Result<SancionResponse?, SancionError>
    suspend fun createSancion(sancion: SancionRequest) : Result<SancionResponse, SancionError>
    suspend fun updateSancion(guid: String, sancion: SancionUpdateRequest) : Result<SancionResponse?, SancionError>
    suspend fun deleteSancionByGuid(guid: String) : Result<SancionResponse?, SancionError>
    suspend fun getByFecha(fecha: LocalDate): Result<List<SancionResponse>, SancionError>
    suspend fun getByTipo(tipo: String): Result<List<SancionResponse>, SancionError>
    suspend fun getSancionByUserGuid(userGuid: String) : Result<List<SancionResponse>, SancionError>
}