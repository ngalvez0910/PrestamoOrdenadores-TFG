package org.example.prestamoordenadores.rest.incidencias.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketHandler
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.config.websockets.models.Notification
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.example.prestamoordenadores.config.websockets.models.NotificationTypeDto
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.example.prestamoordenadores.rest.incidencias.mappers.IncidenciaMapper
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["incidencias"])
class IncidenciaServiceImpl(
    private val repository: IncidenciaRepository,
    private val mapper: IncidenciaMapper,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    @Qualifier("webSocketIncidenciasHandler") private val webSocketHandler: WebSocketHandler,
    private val webService : WebSocketService
) : IncidenciaService {
    override fun getAllIncidencias(page: Int, size: Int): Result<PagedResponse<IncidenciaResponse>, IncidenciaError> {
        logger.debug { "Obteniendo todas las incidencias" }

        val pageRequest = PageRequest.of(page, size)
        val pagesIncidencias = repository.findAll(pageRequest)
        val incidenciaResponses = mapper.toIncidenciaResponseList(pagesIncidencias.content)

        val pagedResponse = PagedResponse(
            content = incidenciaResponses,
            totalElements = pagesIncidencias.totalElements
        )

        return Ok(pagedResponse)
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

        val incidenciaResponse = mapper.toIncidenciaResponse(newIncidencia)
        sendNotificationNuevaIncidencia(newIncidencia, user)

        return Ok(incidenciaResponse)
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

        onChangeAdmin(Notification.Tipo.UPDATE, existingIncidencia)
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

        onChangeAdmin(Notification.Tipo.DELETE, incidencia)
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

    fun onChangeAdmin(tipo: Notification.Tipo?, incidencia: Incidencia) {
        logger.info { "Servicio de Incidencias onChange con tipo: $tipo e incidencia GUID: ${incidencia.guid}" }

        try {
            val incidenciaResponse = mapper.toIncidenciaResponse(incidencia)
            val notificacion = Notification(
                "INCIDENCIAS",
                tipo,
                incidenciaResponse,
                LocalDateTime.now().toString()
            )

            val json = objectMapper.writeValueAsString(notificacion)

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

    private fun sendNotificationNuevaIncidencia(incidencia: Incidencia, user: User) {
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Incidencia Recibida: ${incidencia.guid}",
            mensaje = "Hemos recibido tu reporte sobre '${incidencia.asunto}'. Gracias por informarnos.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.INCIDENCIA,
            enlace = "/incidencias/detalle/${incidencia.guid}"
        )
        webService.createAndSendNotification(user.email, notificacionParaUser)

        val administradores = userRepository.findUsersByRol(Role.ADMIN)

        administradores.forEach { admin ->
            if (admin?.email != user.email) {
                val notificacionParaAdmin = NotificationDto(
                    id = UUID.randomUUID().toString(),
                    titulo = "Nueva Incidencia Reportada: ${incidencia.guid}",
                    mensaje = "El usuario ${user.nombre} ${user.apellidos} ha reportado: '${incidencia.asunto}'.",
                    fecha = LocalDateTime.now(),
                    leida = false,
                    tipo = NotificationTypeDto.INCIDENCIA,
                    enlace = "/admin/incidencias/ver/${incidencia.guid}"
                )
                webService.createAndSendNotification(admin?.email ?: "", notificacionParaAdmin)
            }
        }
    }

}