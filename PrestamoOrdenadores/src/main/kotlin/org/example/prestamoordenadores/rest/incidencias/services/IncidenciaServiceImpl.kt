package org.example.prestamoordenadores.rest.incidencias.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.example.prestamoordenadores.rest.incidencias.mappers.IncidenciaMapper
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
class IncidenciaServiceImpl(
    private val repository: IncidenciaRepository,
    private val mapper: IncidenciaMapper,
    private val userRepository: UserRepository
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
        logger.debug { "Creando incidencia" }
        val newIncidencia = mapper.toIncidenciaFromCreate(incidencia)

        repository.save(newIncidencia)

        return Ok(mapper.toIncidenciaResponse(newIncidencia))
    }

    override fun updateIncidencia(guid: String, incidencia: IncidenciaUpdateRequest): Result<IncidenciaResponse?, IncidenciaError> {
        val existingIncidencia = repository.findIncidenciaByGuid(guid)
        if (existingIncidencia == null) {
            return Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        }

        existingIncidencia.estadoIncidencia = EstadoIncidencia.valueOf(incidencia.estadoIncidencia)
        existingIncidencia.updatedDate = LocalDateTime.now()

        repository.save(existingIncidencia)

        return Ok(mapper.toIncidenciaResponse(existingIncidencia))
    }

    override fun deleteIncidenciaByGuid(guid: String): Result<IncidenciaResponse?, IncidenciaError> {
        logger.debug { "Eliminando incidencia con GUID: $guid" }
        val incidencia = repository.findIncidenciaByGuid(guid)
        if (incidencia == null) {
            return Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        }
        repository.delete(incidencia)

        return Ok(mapper.toIncidenciaResponse(incidencia))
    }

    override fun getIncidenciaByEstado(estado: String): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo incidencias en estado: $estado" }
        val incidencias = repository.findIncidenciasByEstadoIncidencia(EstadoIncidencia.valueOf(estado))

        return Ok(mapper.toIncidenciaResponseList(incidencias))
    }

    override fun getIncidenciasByUserGuid(userGuid: String): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo incidencias de user con GUID: $userGuid" }
        val user = userRepository.findByGuid(userGuid)
        if (user == null) {
            return Err(IncidenciaError.UserNotFound("Usuario no encontrado"))
        }

        val incidencias = repository.findIncidenciasByUserGuid(userGuid)

        return Ok(mapper.toIncidenciaResponseList(incidencias))
    }
}