package org.example.prestamoordenadores.rest.incidencias.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    override suspend fun getAllIncidencias(page: Int, size: Int): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo todas las incidencias" }

        return withContext(Dispatchers.IO) {
            val pageRequest = PageRequest.of(page, size)
            val pagesIncidencias = repository.findAll(pageRequest)
            val incidenciaResponses = mapper.toIncidenciaResponseList(pagesIncidencias.content)

            Ok(incidenciaResponses)
        }
    }

    @Cacheable(key = "#guid")
    override suspend fun getIncidenciaByGuid(guid: String): Result<IncidenciaResponse?, IncidenciaError> {
        logger.debug { "Obteniendo incidencia con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val incidencia = repository.findIncidenciaByGuid(guid)

            if (incidencia == null) {
                Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
            } else {
                Ok(mapper.toIncidenciaResponse(incidencia))
            }
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun createIncidencia(incidencia: IncidenciaCreateRequest): Result<IncidenciaResponse, IncidenciaError> {
        logger.debug { "Creando incidencia" }

        return withContext(Dispatchers.IO) {
            val incidenciaValidada = incidencia.validate()
            if (incidenciaValidada.isErr) {
                return@withContext Err(IncidenciaError.IncidenciaValidationError("Incidencia inválida"))
            }

            val newIncidencia = mapper.toIncidenciaFromCreate(incidencia)
            repository.save(newIncidencia)

            Ok(mapper.toIncidenciaResponse(newIncidencia))
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun updateIncidencia(guid: String, incidencia: IncidenciaUpdateRequest): Result<IncidenciaResponse?, IncidenciaError> {
        return withContext(Dispatchers.IO) {
            val existingIncidencia = repository.findIncidenciaByGuid(guid)
            if (existingIncidencia == null) {
                return@withContext Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
            }

            val incidenciaValidada = incidencia.validate()
            if (incidenciaValidada.isErr) {
                return@withContext Err(IncidenciaError.IncidenciaValidationError("Incidencia inválida"))
            }

            val estadoNormalizado = incidencia.estadoIncidencia.replace(" ", "_").uppercase()

            existingIncidencia.estadoIncidencia = EstadoIncidencia.valueOf(estadoNormalizado)
            existingIncidencia.updatedDate = LocalDateTime.now()

            repository.save(existingIncidencia)

            Ok(mapper.toIncidenciaResponse(existingIncidencia))
        }
    }

    @CachePut(key = "#guid")
    override suspend fun deleteIncidenciaByGuid(guid: String): Result<IncidenciaResponse?, IncidenciaError> {
        logger.debug { "Eliminando incidencia con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val incidencia = repository.findIncidenciaByGuid(guid)
            if (incidencia == null) {
                return@withContext Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
            }

            repository.delete(incidencia)

            Ok(mapper.toIncidenciaResponse(incidencia))
        }
    }

    @Cacheable(key = "#estado")
    override suspend fun getIncidenciaByEstado(estado: String): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo incidencias en estado: $estado" }

        return withContext(Dispatchers.IO) {
            val estadoNormalizado = estado.replace(" ", "_").uppercase()

            val estadoEnum = EstadoIncidencia.entries.find { it.name == estadoNormalizado }
            if (estadoEnum == null) {
                return@withContext Err(IncidenciaError.IncidenciaNotFound("Incidencia con estado '$estado' no encontrada"))
            }

            val incidencias = repository.findIncidenciasByEstadoIncidencia(estadoEnum)

            Ok(mapper.toIncidenciaResponseList(incidencias))
        }
    }

    @Cacheable(key = "#userGuid")
    override suspend fun getIncidenciasByUserGuid(userGuid: String): Result<List<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo incidencias de user con GUID: $userGuid" }

        return withContext(Dispatchers.IO) {
            val user = userRepository.findByGuid(userGuid)
            if (user == null) {
                return@withContext Err(IncidenciaError.UserNotFound("Usuario no encontrado"))
            }

            val incidencias = repository.findIncidenciasByUserGuid(userGuid)

            Ok(mapper.toIncidenciaResponseList(incidencias))
        }
    }
}