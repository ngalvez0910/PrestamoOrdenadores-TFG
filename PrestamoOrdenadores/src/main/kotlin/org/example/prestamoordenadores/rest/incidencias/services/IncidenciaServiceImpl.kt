package org.example.prestamoordenadores.rest.incidencias.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.example.prestamoordenadores.rest.incidencias.mappers.IncidenciaMapper
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service

private val logger = logging()

@Service
class IncidenciaServiceImpl(
    private val repository: IncidenciaRepository,
    private val mapper: IncidenciaMapper
) : IncidenciaService {
    override fun getAllIncidencias(): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo todas las incidencias" }
        var incidencias = repository.findAll()

        return Ok(mapper.toIncidenciaResponseList(incidencias))
    }

    override fun getIncidenciaByGuid(guid: String): Result<IncidenciaResponse?, IncidenciaError> {
        logger.debug { "Obteniendo incidencia con GUID: $guid" }
        val incidencia = repository.findIncidenciaByGuid(guid)

        return if (incidencia == null) {
            Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        } else {
            Ok(mapper.toIncidenciaResponse(incidencia))
        }
    }

    override fun createIncidencia(incidencia: IncidenciaCreateRequest): Result<IncidenciaResponse, IncidenciaError> {
        TODO("Not yet implemented")
    }

    override fun updateIncidencia(
        guid: String,
        incidencia: IncidenciaUpdateRequest
    ): Result<IncidenciaResponse?, IncidenciaError> {
        TODO("Not yet implemented")
    }

    override fun deleteIncidenciaByGuid(guid: String): Result<IncidenciaResponse?, IncidenciaError> {
        TODO("Not yet implemented")
    }

    override fun getIncidenciaByEstado(estado: String): Result<List<IncidenciaResponse>, IncidenciaError> {
        TODO("Not yet implemented")
    }

    override fun getIncidenciasByUserGuid(userGuid: String): Result<List<IncidenciaResponse>, IncidenciaError> {
        TODO("Not yet implemented")
    }
}