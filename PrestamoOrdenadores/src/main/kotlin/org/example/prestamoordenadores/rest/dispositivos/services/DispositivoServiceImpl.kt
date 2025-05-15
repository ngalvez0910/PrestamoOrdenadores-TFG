package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.example.prestamoordenadores.config.websockets.models.NotificationSeverityDto
import org.example.prestamoordenadores.config.websockets.models.NotificationTypeDto
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.rest.dispositivos.mappers.DispositivoMapper
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["dispositivos"])
class DispositivoServiceImpl(
    private val dispositivoRepository: DispositivoRepository,
    private val mapper: DispositivoMapper,
    private val incidenciaRepository: IncidenciaRepository,
    private val userRepository: UserRepository,
    private val webService : WebSocketService
) : DispositivoService {
    override fun getAllDispositivos(page: Int, size: Int): Result<PagedResponse<DispositivoResponseAdmin>, DispositivoError> {
        logger.debug { "Obteniendo todos los dispositivos" }

        val pageRequest = PageRequest.of(page, size)
        val pageDispositivos = dispositivoRepository.findAll(pageRequest)
        val dispositivoResponses = mapper.toDispositivoResponseListAdmin(pageDispositivos.content)

        val pagedResponse = PagedResponse(
            content = dispositivoResponses,
            totalElements = pageDispositivos.totalElements
        )

        return Ok(pagedResponse)
    }

    @Cacheable(key = "#guid")
    override fun getDispositivoByGuid(guid: String): Result<DispositivoResponseAdmin?, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con GUID: $guid" }

        val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)

        return if (dispositivo == null) {
            Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        } else {
            Ok(mapper.toDispositivoResponseAdmin(dispositivo))
        }
    }

    @CachePut(key = "#result.guid")
    override fun createDispositivo(dispositivo: DispositivoCreateRequest): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Guardando un nuevo dispositivo" }

        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            logger.warn { "No hay un usuario autenticado para la creación del dispositivo." }
            return Err(DispositivoError.AuthenticationError("Usuario no autenticado."))
        }

        val principal = authentication.principal
        val userEmail: String = when (principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> {
                logger.warn { "El principal autenticado no es del tipo esperado: ${principal::class.simpleName}" }
                return Err(DispositivoError.AuthenticationError("No se pudo determinar el email del usuario autenticado."))
            }
        }

        val currentUser = userRepository.findByEmail(userEmail)
        if (currentUser == null) {
            logger.error { "No se pudo encontrar al usuario $userEmail en la base de datos." }
            return Err(DispositivoError.UserNotFound("Usuario $userEmail no encontrado."))
        }

        val dispositivoValidado = dispositivo.validate()
        if (dispositivoValidado.isErr) {
            return Err(DispositivoError.DispositivoValidationError("Dispositivo inválido: ${dispositivoValidado.error}"))
        }

        val newDispositivo = mapper.toDispositivoFromCreate(dispositivo)
        dispositivoRepository.save(newDispositivo)

        sendNotificationAñadirStock(newDispositivo, currentUser)

        return Ok(mapper.toDispositivoResponse(newDispositivo))
    }

    @CachePut(key = "#result.guid")
    override fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest): Result<DispositivoResponseAdmin, DispositivoError> {
        logger.debug { "Actualizando dispositivo con GUID: $guid" }

        val existingDispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        if (existingDispositivo == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        }

        if (dispositivo.incidenciaGuid != null) {
            val incidencia = incidenciaRepository.findIncidenciaByGuid(dispositivo.incidenciaGuid!!)
            if (incidencia == null) {
                return Err(DispositivoError.IncidenciaNotFound("Incidencia con GUID: ${dispositivo.incidenciaGuid} no encontrada"))
            }
            existingDispositivo.incidencia = incidencia
        } else {
            existingDispositivo.incidencia = null
        }

        dispositivo.componentes?.let { existingDispositivo.componentes = it }
        dispositivo.estadoDispositivo?.let { existingDispositivo.estadoDispositivo = EstadoDispositivo.valueOf(it) }
        dispositivo.isDeleted?.let { existingDispositivo.isDeleted = it }

        dispositivoRepository.save(existingDispositivo)

        return Ok(mapper.toDispositivoResponseAdmin(existingDispositivo))
    }

    @CachePut(key = "#guid")
    override fun deleteDispositivoByGuid(guid: String): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Eliminando dispositivo con GUID: $guid" }

        val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        if (dispositivo == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        }

        dispositivo.estadoDispositivo = EstadoDispositivo.NO_DISPONIBLE
        dispositivo.isDeleted = true
        dispositivo.updatedDate = LocalDateTime.now()

        dispositivoRepository.save(dispositivo)

        return Ok(mapper.toDispositivoResponse(dispositivo))
    }

    @Cacheable(key = "#numeroSerie")
    override fun getDispositivoByNumeroSerie(numeroSerie: String): Result<DispositivoResponseAdmin, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con numero de serie: $numeroSerie" }

        val dispositivo = dispositivoRepository.findByNumeroSerie(numeroSerie)

        return if (dispositivo == null) {
            Err(DispositivoError.DispositivoNotFound("Dispositivo con numero de serie: $numeroSerie no encontrado"))
        } else {
            Ok(mapper.toDispositivoResponseAdmin(dispositivo))
        }
    }

    @Cacheable(key = "#estado")
    override fun getDispositivoByEstado(estado: String): Result<List<DispositivoResponseAdmin>, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con estado: $estado" }

        val estadoNormalizado = estado.replace(" ", "_").uppercase()
        val estadoEnum = EstadoDispositivo.entries.find { it.name == estadoNormalizado }

        if (estadoEnum == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con estado '$estado' no encontrado"))
        }

        val dispositivos = dispositivoRepository.findByEstadoDispositivo(estadoEnum)

        return Ok(mapper.toDispositivoResponseListAdmin(dispositivos))
    }

    override fun getStock(): Result<Int, DispositivoError> {
        logger.debug { "Obteniendo stock" }

        return Ok(dispositivoRepository.findAll().count())
    }

    private fun sendNotificationAñadirStock(dispositivo: Dispositivo, user: User) {
        val notificacionParaAdminQueAñadió = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Nuevo Dispositivo: ${dispositivo.numeroSerie}",
            mensaje = "Has añadido un dispositivo con numero de serie: '${dispositivo.numeroSerie}'.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.INFO,
            enlace = "/admin/dispositivo/detalle/${dispositivo.guid}",
            severidadSugerida = NotificationSeverityDto.SUCCESS
        )
        logger.debug { "Preparando notificación de confirmación de adición para admin (${user.email}): $notificacionParaAdminQueAñadió" }
        webService.createAndSendNotification(user.email, notificacionParaAdminQueAñadió)


        val administradores = userRepository.findUsersByRol(Role.ADMIN).filter { it?.email != user.email }

        if (administradores.isNotEmpty()) {
            logger.info { "Se encontraron ${administradores.size} otros administradores para notificar sobre la resolución." }
            administradores.forEach { otroAdmin ->
                if (otroAdmin != null) {
                    val notificacionParaOtroAdmin = NotificationDto(
                        id = UUID.randomUUID().toString(),
                        titulo = "Dispositivo Añadido: ${dispositivo.numeroSerie}",
                        mensaje = "El dispositivo con numero de serie: '${dispositivo.numeroSerie}' fue añadido por ${user.nombre} ${user.apellidos}.",
                        fecha = LocalDateTime.now(),
                        leida = false,
                        tipo = NotificationTypeDto.INFO,
                        enlace = "/admin/dispositivo/detalle/${dispositivo.guid}",
                        severidadSugerida = NotificationSeverityDto.INFO
                    )
                    logger.debug { "Preparando notificación informativa de adición para otro admin (${otroAdmin.email}): $notificacionParaOtroAdmin" }
                    webService.createAndSendNotification(otroAdmin.email, notificacionParaOtroAdmin)
                }
            }
        }
    }
}