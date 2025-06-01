package org.example.prestamoordenadores.rest.incidencias.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.example.prestamoordenadores.config.websockets.models.NotificationSeverityDto
import org.example.prestamoordenadores.config.websockets.models.NotificationTypeDto
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponseAdmin
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
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

private val logger = logging()

/**
 * Implementación del servicio de gestión de incidencias.
 *
 * Proporciona la lógica de negocio para las operaciones CRUD y consultas relacionadas
 * con las incidencias, incluyendo la interacción con la base de datos, mapeo de DTOs,
 * manejo de errores, y notificaciones vía WebSocket.
 *
 * @property repository Repositorio para operaciones de base de datos de [Incidencia].
 * @property mapper Mapeador para convertir entre entidades [Incidencia] y DTOs.
 * @property userRepository Repositorio para operaciones de base de datos de [User].
 * @property webService Servicio para el envío de notificaciones WebSocket.
 * @author Natalia González Álvarez
 */
@Service
@CacheConfig(cacheNames = ["incidencias"])
class IncidenciaServiceImpl(
    private val repository: IncidenciaRepository,
    private val mapper: IncidenciaMapper,
    private val userRepository: UserRepository,
    private val webService : WebSocketService
) : IncidenciaService {

    /**
     * Obtiene una lista paginada de todas las incidencias.
     *
     * @param page El número de página (basado en 0).
     * @param size El tamaño de la página.
     * @return Un [Result.Ok] con un [PagedResponse] de [IncidenciaResponse] si es exitoso.
     * @author Natalia González Álvarez
     */
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

    /**
     * Obtiene una incidencia por su identificador único global (GUID) para una vista de usuario.
     *
     * Este método está cacheado.
     *
     * @param guid El GUID de la incidencia a buscar.
     * @return Un [Result.Ok] con el [IncidenciaResponse] si se encuentra, o un [Result.Err] con [IncidenciaError.IncidenciaNotFound] si no.
     * @author Natalia González Álvarez
     */
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

    /**
     * Obtiene una incidencia por su identificador único global (GUID) para una vista administrativa.
     *
     * Este método está cacheado.
     *
     * @param guid El GUID de la incidencia a buscar.
     * @return Un [Result.Ok] con el [IncidenciaResponseAdmin] si se encuentra, o un [Result.Err] con [IncidenciaError.IncidenciaNotFound] si no.
     * @author Natalia González Álvarez
     */
    @Cacheable(key = "#guid")
    override fun getIncidenciaByGuidAdmin(guid: String): Result<IncidenciaResponseAdmin?, IncidenciaError> {
        logger.debug { "Obteniendo incidencia con GUID: $guid" }

        val incidencia = repository.findIncidenciaByGuid(guid)

        return if (incidencia == null) {
            Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        } else {
            Ok(mapper.toIncidenciaResponseAdmin(incidencia))
        }
    }

    /**
     * Crea una nueva incidencia.
     *
     * Valida la solicitud, obtiene el usuario autenticado y guarda la nueva incidencia.
     * Envía notificaciones a los administradores y al usuario que reportó la incidencia.
     * Este método actualiza la caché tras la creación.
     *
     * @param incidencia La solicitud [IncidenciaCreateRequest] con los datos de la nueva incidencia.
     * @return Un [Result.Ok] con el [IncidenciaResponse] del dispositivo creado si es exitoso,
     * o un [Result.Err] con un [IncidenciaError] si falla la validación o el usuario no es encontrado.
     * @author Natalia González Álvarez
     */
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

    /**
     * Actualiza el estado de una incidencia existente.
     *
     * Requiere que un administrador esté autenticado. Normaliza el estado de entrada
     * y actualiza la incidencia en la base de datos. Envía notificaciones
     * al usuario que reportó la incidencia y a los administradores.
     * Este método actualiza la caché tras la actualización.
     *
     * @param guid El GUID de la incidencia a actualizar.
     * @param incidencia La solicitud [IncidenciaUpdateRequest] con el nuevo estado.
     * @return Un [Result.Ok] con el [IncidenciaResponse] actualizado si es exitoso,
     * o un [Result.Err] con [IncidenciaError.IncidenciaNotFound] si no se encuentra la incidencia,
     * [IncidenciaError.UserNotFound] si el administrador no es encontrado,
     * o [IncidenciaError.IncidenciaValidationError] si el estado es inválido.
     * @author Natalia González Álvarez
     */
    @CachePut(key = "#result.guid")
    override fun updateIncidencia(guid: String, incidencia: IncidenciaUpdateRequest): Result<IncidenciaResponse?, IncidenciaError> {
        val authentication = SecurityContextHolder.getContext().authentication
        val emailAdmin = authentication.name
        val adminUpdating = userRepository.findByEmail(emailAdmin)
        if (adminUpdating == null) {
            return Err(IncidenciaError.UserNotFound("No se encontró el usuario con email: $emailAdmin"))
        }

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

        sendNotificationActualizacionIncidencia(existingIncidencia, adminUpdating)
        return Ok(mapper.toIncidenciaResponse(existingIncidencia))
    }

    /**
     * Elimina lógicamente una incidencia por su GUID, marcándola como eliminada.
     *
     * Requiere que un administrador esté autenticado. Actualiza el estado de la incidencia
     * a `isDeleted = true` y su fecha de actualización. Envía notificaciones a los administradores.
     * Este método invalida la entrada en caché correspondiente al GUID.
     *
     * @param guid El GUID de la incidencia a eliminar lógicamente.
     * @return Un [Result.Ok] con el [IncidenciaResponseAdmin] del dispositivo modificado si es exitoso,
     * o un [Result.Err] con [IncidenciaError.IncidenciaNotFound] si la incidencia no se encuentra,
     * o [IncidenciaError.UserNotFound] si el administrador no es encontrado.
     * @author Natalia González Álvarez
     */
    @CacheEvict
    override fun deleteIncidenciaByGuid(guid: String): Result<IncidenciaResponseAdmin?, IncidenciaError> {
        val authentication = SecurityContextHolder.getContext().authentication
        val emailAdmin = authentication.name
        val adminDeleting = userRepository.findByEmail(emailAdmin)
        if (adminDeleting == null) {
            return Err(IncidenciaError.UserNotFound("No se encontró el usuario con email: $emailAdmin"))
        }

        logger.debug { "Eliminando incidencia con GUID: $guid" }

        val incidencia = repository.findIncidenciaByGuid(guid)
        if (incidencia == null) {
            return Err(IncidenciaError.IncidenciaNotFound("Incidencia no encontrada"))
        }

        incidencia.isDeleted = true
        incidencia.updatedDate = LocalDateTime.now()

        repository.save(incidencia)

        sendNotificationEliminacionIncidencia(incidencia, adminDeleting)
        return Ok(mapper.toIncidenciaResponseAdmin(incidencia))
    }

    /**
     * Obtiene una lista de incidencias por su estado.
     *
     * Normaliza el estado de entrada para que coincida con los valores del enum [EstadoIncidencia].
     * Este método está cacheado.
     *
     * @param estado La cadena de texto que representa el estado de la incidencia (ej. "PENDIENTE", "RESUELTO").
     * @return Un [Result.Ok] con una lista de [IncidenciaResponse] si es exitoso,
     * o un [Result.Err] con [IncidenciaError.IncidenciaNotFound] si el estado proporcionado no es válido.
     * @author Natalia González Álvarez
     */
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

    /**
     * Obtiene una lista de incidencias asociadas a un usuario específico por su GUID.
     *
     * Este método está cacheado.
     *
     * @param userGuid El GUID del usuario cuyas incidencias se desean buscar.
     * @return Un [Result.Ok] con una lista de [IncidenciaResponse] si es exitoso,
     * o un [Result.Err] con [IncidenciaError.UserNotFound] si el usuario no es encontrado.
     * @author Natalia González Álvarez
     */
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

    /**
     * Envía notificaciones WebSocket cuando se crea una nueva incidencia.
     *
     * Envía una notificación de confirmación al usuario que reportó la incidencia
     * y notificaciones informativas a todos los administradores.
     *
     * @param incidencia La [Incidencia] que ha sido creada.
     * @param user El [User] que reportó la incidencia.
     * @author Natalia González Álvarez
     */
    private fun sendNotificationNuevaIncidencia(incidencia: Incidencia, user: User) {
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Incidencia Recibida: ${incidencia.guid}",
            mensaje = "Hemos recibido tu reporte sobre '${incidencia.asunto}'. Gracias por informarnos.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.INCIDENCIA,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.SUCCESS,
            mostrarToast = true
        )
        webService.createAndSendNotification(user.email, notificacionParaUser)

        val administradores = userRepository.findUsersByRol(Role.ADMIN)

        administradores.forEach { admin ->
            if (admin?.email != user.email) { // Evitar enviarle al mismo usuario si es admin y reportó
                val notificacionParaAdmin = NotificationDto(
                    id = UUID.randomUUID().toString(),
                    titulo = "Nueva Incidencia Reportada: ${incidencia.guid}",
                    mensaje = "El usuario ${user.nombre} ${user.apellidos} ha reportado: '${incidencia.asunto}'.",
                    fecha = LocalDateTime.now(),
                    leida = false,
                    tipo = NotificationTypeDto.INCIDENCIA,
                    enlace = "/admin/incidencia/detalle/${incidencia.guid}",
                    severidadSugerida = NotificationSeverityDto.INFO,
                    mostrarToast = true
                )
                webService.createAndSendNotification(admin?.email ?: "", notificacionParaAdmin)
            }
        }
    }

    /**
     * Envía notificaciones WebSocket cuando se actualiza una incidencia (ej. se resuelve).
     *
     * Notifica al usuario que reportó la incidencia que ha sido resuelta,
     * al administrador que la resolvió, y a los demás administradores.
     *
     * @param incidencia La [Incidencia] que ha sido actualizada.
     * @param user El [User] (administrador) que realizó la actualización.
     * @author Natalia González Álvarez
     */
    private fun sendNotificationActualizacionIncidencia(incidencia: Incidencia, user: User) {
        val reportante = incidencia.user

        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "¡Hemos Resuelto la Incidencia que Reportaste!",
            mensaje = "La incidencia '${incidencia.asunto}' que reportaste ha sido resuelta. ¡Gracias!",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SISTEMA,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.INFO,
            mostrarToast = true
        )
        webService.createAndSendNotification(reportante.email, notificacionParaUser)

        val notificacionParaAdminQueResolvio = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Resolviste Incidencia: ${incidencia.guid}",
            mensaje = "Has marcado como resuelta la incidencia '${incidencia.asunto}'.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.INCIDENCIA,
            enlace = "/admin/incidencia/detalle/${incidencia.guid}",
            severidadSugerida = NotificationSeverityDto.SUCCESS,
            mostrarToast = true
        )
        logger.debug { "Preparando notificación de confirmación de resolución para admin (${user.email}): $notificacionParaAdminQueResolvio" }
        webService.createAndSendNotification(user.email, notificacionParaAdminQueResolvio)


        val administradores = userRepository.findUsersByRol(Role.ADMIN).filter { it?.email != user.email }

        if (administradores.isNotEmpty()) {
            logger.info { "Se encontraron ${administradores.size} otros administradores para notificar sobre la resolución." }
            administradores.forEach { otroAdmin ->
                if (otroAdmin != null) {
                    val notificacionParaOtroAdmin = NotificationDto(
                        id = UUID.randomUUID().toString(),
                        titulo = "Incidencia Resuelta por: ${incidencia.guid}",
                        mensaje = "La incidencia '${incidencia.asunto}' fue marcada como resuelta por ${user.nombre} ${user.apellidos}.",
                        fecha = LocalDateTime.now(),
                        leida = false,
                        tipo = NotificationTypeDto.INCIDENCIA,
                        enlace = "/admin/incidencia/detalle/${incidencia.guid}",
                        severidadSugerida = NotificationSeverityDto.INFO,
                        mostrarToast = false
                    )
                    logger.debug { "Preparando notificación informativa de resolución para otro admin (${otroAdmin.email}): $notificacionParaOtroAdmin" }
                    webService.createAndSendNotification(otroAdmin.email, notificacionParaOtroAdmin)
                }
            }
        }
    }

    /**
     * Envía notificaciones WebSocket cuando se elimina lógicamente una incidencia.
     *
     * Notifica al administrador que realizó la eliminación y a los demás administradores.
     *
     * @param incidencia La [Incidencia] que ha sido eliminada lógicamente.
     * @param user El [User] (administrador) que realizó la eliminación.
     * @author Natalia González Álvarez
     */
    private fun sendNotificationEliminacionIncidencia(incidencia: Incidencia, user: User) {
        val notificacionAdminElimina = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Eliminaste Incidencia: ${incidencia.guid}",
            mensaje = "Has eliminado correctamente la incidencia '${incidencia.asunto}'.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SISTEMA,
            enlace = "/admin/incidencia/detalle/${incidencia.guid}",
            severidadSugerida = NotificationSeverityDto.SUCCESS,
            mostrarToast = false
        )
        logger.debug { "Preparando notificación de confirmación de eliminacion para admin (${user.email}): $notificacionAdminElimina" }
        webService.createAndSendNotification(user.email, notificacionAdminElimina)


        val administradores = userRepository.findUsersByRol(Role.ADMIN).filter { it?.email != user.email }

        if (administradores.isNotEmpty()) {
            logger.info { "Se encontraron ${administradores.size} otros administradores para notificar sobre la resolución." }
            administradores.forEach { otroAdmin ->
                if (otroAdmin != null) {
                    val notificacionParaOtroAdmin = NotificationDto(
                        id = UUID.randomUUID().toString(),
                        titulo = "Incidencia Eliminada por: ${incidencia.guid}",
                        mensaje = "La incidencia '${incidencia.asunto}' fue eliminada por ${user.nombre} ${user.apellidos}.",
                        fecha = LocalDateTime.now(),
                        leida = false,
                        tipo = NotificationTypeDto.SISTEMA,
                        enlace = "/admin/incidencia/detalle/${incidencia.guid}",
                        severidadSugerida = NotificationSeverityDto.INFO,
                        mostrarToast = false
                    )
                    logger.debug { "Preparando notificación informativa de eliminaicon para otro admin (${otroAdmin.email}): $notificacionParaOtroAdmin" }
                    webService.createAndSendNotification(otroAdmin.email, notificacionParaOtroAdmin)
                }
            }
        }
    }
}