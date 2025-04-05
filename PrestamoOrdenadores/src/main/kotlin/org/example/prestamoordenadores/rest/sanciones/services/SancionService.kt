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
    fun getAllSanciones(page: Int, size: Int): Result<List<SancionResponse>, SancionError>
    fun getSancionByGuid(guid: String) : Result<SancionResponse?, SancionError>
    fun createSancion(sancion: SancionRequest) : Result<SancionResponse, SancionError>
    fun updateSancion(guid: String, sancion: SancionUpdateRequest) : Result<SancionResponse?, SancionError>
    fun deleteSancionByGuid(guid: String) : Result<SancionResponse?, SancionError>
    fun getByFecha(fecha: LocalDate): Result<List<SancionResponse>, SancionError>
    fun getByTipo(tipo: String): Result<List<SancionResponse>, SancionError>
    fun getSancionByUserGuid(userGuid: String) : Result<List<SancionResponse>, SancionError>
}