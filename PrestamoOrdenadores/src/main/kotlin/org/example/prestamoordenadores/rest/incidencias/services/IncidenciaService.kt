package org.example.prestamoordenadores.rest.incidencias.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.springframework.stereotype.Service

@Service
interface IncidenciaService {
    suspend fun getAllIncidencias(page: Int, size: Int): Result<List<IncidenciaResponse>, IncidenciaError>
    suspend fun getIncidenciaByGuid(guid: String) : Result<IncidenciaResponse?, IncidenciaError>
    suspend fun createIncidencia(incidencia: IncidenciaCreateRequest) : Result<IncidenciaResponse, IncidenciaError>
    suspend fun updateIncidencia(guid: String, incidencia: IncidenciaUpdateRequest) : Result<IncidenciaResponse?, IncidenciaError>
    suspend fun deleteIncidenciaByGuid(guid: String) : Result<IncidenciaResponse?, IncidenciaError>
    suspend fun getIncidenciaByEstado(estado: String): Result<List<IncidenciaResponse>, IncidenciaError>
    suspend fun getIncidenciasByUserGuid(userGuid: String) : Result<List<IncidenciaResponse>, IncidenciaError>
}