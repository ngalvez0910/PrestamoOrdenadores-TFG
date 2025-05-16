package org.example.prestamoordenadores.rest.incidencias.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponseAdmin
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service

@Service
interface IncidenciaService {
    fun getAllIncidencias(page: Int, size: Int): Result<PagedResponse<IncidenciaResponse>, IncidenciaError>
    fun getIncidenciaByGuid(guid: String) : Result<IncidenciaResponse?, IncidenciaError>
    fun getIncidenciaByGuidAdmin(guid: String) : Result<IncidenciaResponseAdmin?, IncidenciaError>
    fun createIncidencia(incidencia: IncidenciaCreateRequest) : Result<IncidenciaResponse, IncidenciaError>
    fun updateIncidencia(guid: String, incidencia: IncidenciaUpdateRequest) : Result<IncidenciaResponse?, IncidenciaError>
    fun deleteIncidenciaByGuid(guid: String) : Result<IncidenciaResponseAdmin?, IncidenciaError>
    fun getIncidenciaByEstado(estado: String): Result<List<IncidenciaResponse>, IncidenciaError>
    fun getIncidenciasByUserGuid(userGuid: String) : Result<List<IncidenciaResponse>, IncidenciaError>
}