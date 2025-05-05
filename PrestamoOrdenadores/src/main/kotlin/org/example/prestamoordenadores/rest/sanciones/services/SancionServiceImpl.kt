package org.example.prestamoordenadores.rest.sanciones.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketHandler
import org.example.prestamoordenadores.config.websockets.models.Notification
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.example.prestamoordenadores.rest.sanciones.mappers.SancionMapper
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["sanciones"])
class SancionServiceImpl(
    private val repository : SancionRepository,
    private val mapper: SancionMapper,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    @Qualifier("webSocketSancionesHandler") private val webSocketHandler: WebSocketHandler
) : SancionService {
    override fun getAllSanciones(page: Int, size: Int): Result<PagedResponse<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo todas las sanciones" }

        val pageRequest = PageRequest.of(page, size)
        val pageSanciones = repository.findAll(pageRequest)
        val sancionResponses = mapper.toSancionResponseList(pageSanciones.content)

        val pagedResponse = PagedResponse(
            content = sancionResponses,
            totalElements = pageSanciones.totalElements
        )

        return Ok(pagedResponse)
    }

    @Cacheable(key = "#guid")
    override fun getSancionByGuid(guid: String): Result<SancionResponse?, SancionError> {
        logger.debug { "Obteniendo sancion con GUID: $guid" }

        val sancion = repository.findByGuid(guid)

        return if (sancion == null) {
            Err(SancionError.SancionNotFound(guid))
        } else {
            Ok(mapper.toSancionResponse(sancion))
        }
    }

    @CachePut(key = "#result.guid")
    override fun createSancion(sancion: SancionRequest): Result<SancionResponse, SancionError> {
        logger.debug { "Creando nueva sancion" }

        val sancionValidada = sancion.validate()
        if (sancionValidada.isErr) {
            return Err(SancionError.SancionValidationError("Sanción inválida"))
        }

        val user = userRepository.findByGuid(sancion.userGuid)
        if (user == null) {
            return Err(SancionError.UserNotFound("Usuario con GUID: ${sancion.userGuid} no encontrado"))
        }

        val tipoNormalizado = sancion.tipoSancion.replace(" ", "_").uppercase()
        sancion.tipoSancion = tipoNormalizado

        val sancionCreada = mapper.toSancionFromRequest(sancion, userRepository)
        repository.save(sancionCreada)

        onChange(Notification.Tipo.CREATE, sancionCreada)
        return Ok(mapper.toSancionResponse(sancionCreada))
    }

    @CachePut(key = "#result.guid")
    override fun updateSancion(guid: String, sancion: SancionUpdateRequest): Result<SancionResponse?, SancionError> {
        logger.debug { "Buscando sancion" }

        val sancionValidada = sancion.validate()
        if (sancionValidada.isErr) {
            return Err(SancionError.SancionValidationError("Sanción inválida"))
        }

        val existingSancion = repository.findByGuid(guid)
        if (existingSancion == null) {
            return Err(SancionError.SancionNotFound("Sancion no encontrada"))
        }

        logger.debug { "Actualizando sancion" }
        val tipoNormalizado = sancion.tipoSancion.replace(" ", "_").uppercase()

        existingSancion.tipoSancion = TipoSancion.valueOf(tipoNormalizado)
        existingSancion.updatedDate = LocalDateTime.now()

        repository.save(existingSancion)

        onChange(Notification.Tipo.UPDATE, existingSancion)
        return Ok(mapper.toSancionResponse(existingSancion))
    }

    @CachePut(key = "#guid")
    override fun deleteSancionByGuid(guid: String): Result<SancionResponse?, SancionError> {
        logger.debug { "Buscando sancion" }

        val existingSancion = repository.findByGuid(guid)
        if (existingSancion == null) {
            return Err(SancionError.SancionNotFound("Sancion no encontrada"))
        }

        logger.debug { "Eliminando sancion" }
        repository.delete(existingSancion)

        onChange(Notification.Tipo.DELETE, existingSancion)
        return Ok(mapper.toSancionResponse(existingSancion))
    }

    @Cacheable(key = "#fecha")
    override fun getByFecha(fecha: LocalDate): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo sanciones con fecha: $fecha" }

        val sanciones = repository.findSancionByFechaSancion(fecha)
        return Ok(mapper.toSancionResponseList(sanciones))
    }

    @Cacheable(key = "#tipo")
    override fun getByTipo(tipo: String): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo sanciones de tipo: $tipo" }

        val tipoNormalizado = tipo.replace(" ", "_").uppercase()

        val tipoEnum = TipoSancion.entries.find { it.name == tipoNormalizado }
        if (tipoEnum == null) {
            return Err(SancionError.SancionNotFound("Sancion de tipo '$tipo' no encontrada"))
        }

        val sanciones = repository.findSancionByTipoSancion(tipoEnum)

        return Ok(mapper.toSancionResponseList(sanciones))
    }

    @Cacheable(key = "#userGuid")
    override fun getSancionByUserGuid(userGuid: String): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo sanciones de user con GUID: $userGuid" }

        val user = userRepository.findByGuid(userGuid)
        if (user == null) {
            return Err(SancionError.UserNotFound("Usuario no encontrado"))
        }

        val sanciones = repository.findByUserGuid(userGuid)

        return Ok(mapper.toSancionResponseList(sanciones))
    }

    fun onChange(tipo: Notification.Tipo?, sancion: Sancion) {
        logger.info { "Servicio de Sanciones onChange con tipo: $tipo y sancion GUID: ${sancion.guid}" }

        try {
            val sancionResponse = mapper.toSancionResponse(sancion)
            val notificacion = Notification(
                "SANCIONES",
                tipo,
                sancionResponse,
                LocalDateTime.now().toString()
            )

            val json = objectMapper.writeValueAsString(notificacion)

            val creatorUsername = sancion.user.username
            sendMessageUser(creatorUsername, json)

            val adminUsername = getAdminUsername()
            sendMessageUser(adminUsername, json)
        } catch (e: JsonProcessingException) {
            logger.error { "Error al convertir la notificación a JSON" }
        }
    }

    private fun getAdminUsername(): String? {
        return userRepository.findUsersByRol(Role.ADMIN).firstOrNull()?.getUsername()
    }

    private fun sendMessageUser(userName: String?, json: String?) {
        logger.info { "Enviando mensaje WebSocket al usuario: $userName" }
        if (!userName.isNullOrBlank() && !json.isNullOrBlank()) {
            try {
                webSocketHandler.sendMessageToUser(userName, json)
            } catch (e: Exception) {
                logger.error { "Error al enviar el mensaje WebSocket al usuario $userName" }
            }
        } else {
            logger.warn { "No se puede enviar el mensaje WebSocket. Nombre de usuario o JSON nulo/vacío." }
        }
    }
}