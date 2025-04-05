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
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["incidencias"])
class IncidenciaServiceImpl(
    private val repository: IncidenciaRepository,
    private val mapper: IncidenciaMapper,
    private val userRepository: UserRepository
) : IncidenciaService {
    override fun getAllIncidencias(page: Int, size: Int): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo todas las incidencias" }

        val pageRequest = PageRequest.of(page, size)
        val pagesIncidencias = repository.findAll(pageRequest)
        val incidenciaResponses = mapper.toIncidenciaResponseList(pagesIncidencias.content)

        return Ok(incidenciaResponses)
    }

    @Cacheable(key = "#guid")
    override fun getIncidenciaByGuid(guid: String): Result<IncidenciaResponse?, IncidenciaError> {
        logger.debug { "Obteniendo incidencia con GUID: $guid" }

        val incidencia = repository.findIncidenciaByGuid(guid)

        return if (incidencia == null) {
            Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        } else {
            Ok(mapper.toIncidenciaResponse(incidencia))
        }
    }

    @CachePut(key = "#result.guid")
    override fun createIncidencia(incidencia: IncidenciaCreateRequest): Result<IncidenciaResponse, IncidenciaError> {
        logger.debug { "Creando incidencia" }

        val incidenciaValidada = incidencia.validate()
        if (incidenciaValidada.isErr) {
            return Err(IncidenciaError.IncidenciaValidationError("Incidencia inválida"))
        }

        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name

        val user = userRepository.findByEmail(email)
            ?: return Err(IncidenciaError.UserNotFound("No se encontró el usuario con email: $email"))

        val newIncidencia = mapper.toIncidenciaFromCreate(incidencia, user)
        repository.save(newIncidencia)

        return Ok(mapper.toIncidenciaResponse(newIncidencia))
    }

    @CachePut(key = "#result.guid")
    override fun updateIncidencia(guid: String, incidencia: IncidenciaUpdateRequest): Result<IncidenciaResponse?, IncidenciaError> {
        val existingIncidencia = repository.findIncidenciaByGuid(guid)
        if (existingIncidencia == null) {
            return Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        }

        val incidenciaValidada = incidencia.validate()
        if (incidenciaValidada.isErr) {
            return Err(IncidenciaError.IncidenciaValidationError("Incidencia inválida"))
        }

        val estadoNormalizado = incidencia.estadoIncidencia.replace(" ", "_").uppercase()

        existingIncidencia.estadoIncidencia = EstadoIncidencia.valueOf(estadoNormalizado)
        existingIncidencia.updatedDate = LocalDateTime.now()

        repository.save(existingIncidencia)

        return Ok(mapper.toIncidenciaResponse(existingIncidencia))
    }

    @CachePut(key = "#guid")
    override fun deleteIncidenciaByGuid(guid: String): Result<IncidenciaResponse?, IncidenciaError> {
        logger.debug { "Eliminando incidencia con GUID: $guid" }

        val incidencia = repository.findIncidenciaByGuid(guid)
        if (incidencia == null) {
            return Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        }

        repository.delete(incidencia)

        return Ok(mapper.toIncidenciaResponse(incidencia))
    }

    @Cacheable(key = "#estado")
    override fun getIncidenciaByEstado(estado: String): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo incidencias en estado: $estado" }

        val estadoNormalizado = estado.replace(" ", "_").uppercase()

        val estadoEnum = EstadoIncidencia.entries.find { it.name == estadoNormalizado }
        if (estadoEnum == null) {
            return Err(IncidenciaError.IncidenciaNotFound("Incidencia con estado '$estado' no encontrada"))
        }

        val incidencias = repository.findIncidenciasByEstadoIncidencia(estadoEnum)

        return Ok(mapper.toIncidenciaResponseList(incidencias))
    }

    @Cacheable(key = "#userGuid")
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