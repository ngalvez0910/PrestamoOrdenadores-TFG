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
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

private val logger = logging()

/**
 * Implementación del servicio de gestión de dispositivos.
 *
 * Proporciona la lógica de negocio para las operaciones CRUD y consultas relacionadas
 * con los dispositivos, incluyendo la interacción con la base de datos, mapeo de DTOs,
 * manejo de errores, y notificaciones vía WebSocket.
 *
 * @property dispositivoRepository Repositorio para operaciones de base de datos de [Dispositivo].
 * @property mapper Mapeador para convertir entre entidades [Dispositivo] y DTOs.
 * @property incidenciaRepository Repositorio para operaciones de base de datos de [Incidencia].
 * @property userRepository Repositorio para operaciones de base de datos de [User].
 * @property webService Servicio para el envío de notificaciones WebSocket.
 * @author Natalia González Álvarez
 */
@Service
@CacheConfig(cacheNames = ["dispositivos"])
class DispositivoServiceImpl(
    private val dispositivoRepository: DispositivoRepository,
    private val mapper: DispositivoMapper,
    private val incidenciaRepository: IncidenciaRepository,
    private val userRepository: UserRepository,
    private val webService : WebSocketService
) : DispositivoService {

    /**
     * Obtiene una lista paginada de todos los dispositivos, incluyendo información administrativa.
     *
     * @param page El número de página (basado en 0).
     * @param size El tamaño de la página.
     * @return Un [Result.Ok] con un [PagedResponse] de [DispositivoResponseAdmin] si es exitoso.
     * @author Natalia González Álvarez
     */
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

    /**
     * Obtiene un dispositivo por su identificador único global (GUID).
     *
     * Este método está cacheado.
     *
     * @param guid El GUID del dispositivo a buscar.
     * @return Un [Result.Ok] con el [DispositivoResponseAdmin] si se encuentra, o un [Result.Err] con [DispositivoError.DispositivoNotFound] si no.
     * @author Natalia González Álvarez
     */
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

    /**
     * Crea un nuevo dispositivo.
     *
     * Requiere que un usuario esté autenticado. Valida la solicitud y guarda el nuevo dispositivo.
     * Envía notificaciones a los administradores sobre la adición del nuevo dispositivo.
     * Este método actualiza la caché tras la creación.
     *
     * @param dispositivo La solicitud [DispositivoCreateRequest] con los datos del nuevo dispositivo.
     * @return Un [Result.Ok] con el [DispositivoResponse] del dispositivo creado si es exitoso,
     * o un [Result.Err] con un [DispositivoError] si falla la autenticación, el usuario no es encontrado,
     * o la validación del dispositivo falla.
     * @author Natalia González Álvarez
     */
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

    /**
     * Actualiza parcialmente un dispositivo existente.
     *
     * Si se proporciona un `incidenciaGuid`, intenta vincular el dispositivo a esa incidencia.
     * Este método actualiza la caché tras la actualización.
     *
     * @param guid El GUID del dispositivo a actualizar.
     * @param dispositivo La solicitud [DispositivoUpdateRequest] con los campos a actualizar.
     * @return Un [Result.Ok] con el [DispositivoResponseAdmin] actualizado si es exitoso,
     * o un [Result.Err] con [DispositivoError.DispositivoNotFound] si no se encuentra el dispositivo,
     * o [DispositivoError.IncidenciaNotFound] si la incidencia referenciada no existe.
     * @author Natalia González Álvarez
     */
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

    /**
     * Elimina lógicamente un dispositivo, marcándolo como "NO_DISPONIBLE" y `isDeleted = true`.
     *
     * Este método invalida la entrada en caché correspondiente al GUID.
     *
     * @param guid El GUID del dispositivo a eliminar lógicamente.
     * @return Un [Result.Ok] con el [DispositivoResponseAdmin] del dispositivo modificado si es exitoso,
     * o un [Result.Err] con [DispositivoError.DispositivoNotFound] si el dispositivo no se encuentra.
     * @author Natalia González Álvarez
     */
    @CacheEvict
    override fun deleteDispositivoByGuid(guid: String): Result<DispositivoResponseAdmin, DispositivoError> {
        logger.debug { "Eliminando dispositivo con GUID: $guid" }

        val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        if (dispositivo == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        }

        dispositivo.estadoDispositivo = EstadoDispositivo.NO_DISPONIBLE
        dispositivo.isDeleted = true
        dispositivo.updatedDate = LocalDateTime.now()

        dispositivoRepository.save(dispositivo)

        return Ok(mapper.toDispositivoResponseAdmin(dispositivo))
    }

    /**
     * Obtiene un dispositivo por su número de serie.
     *
     * Este método está cacheado.
     *
     * @param numeroSerie El número de serie del dispositivo a buscar.
     * @return Un [Result.Ok] con el [DispositivoResponseAdmin] si se encuentra, o un [Result.Err] con [DispositivoError.DispositivoNotFound] si no.
     * @author Natalia González Álvarez
     */
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

    /**
     * Obtiene una lista de dispositivos filtrados por su estado.
     *
     * Normaliza el estado de entrada para que coincida con los valores del enum [EstadoDispositivo].
     * Este método está cacheado.
     *
     * @param estado La cadena de texto que representa el estado del dispositivo (ej. "DISPONIBLE", "PRESTADO").
     * @return Un [Result.Ok] con una lista de [DispositivoResponseAdmin] si es exitoso,
     * o un [Result.Err] con [DispositivoError.DispositivoNotFound] si el estado proporcionado no es válido.
     * @author Natalia González Álvarez
     */
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

    /**
     * Obtiene el número total de dispositivos en stock (todos los dispositivos, independientemente de su estado).
     *
     * @return Un [Result.Ok] con el número total de dispositivos.
     * @author Natalia González Álvarez
     */
    override fun getStock(): Result<Int, DispositivoError> {
        logger.debug { "Obteniendo stock" }

        return Ok(dispositivoRepository.findAll().count())
    }

    /**
     * Envía notificaciones WebSocket a los administradores cuando se añade un nuevo dispositivo.
     *
     * Envía una notificación de confirmación al administrador que realizó la acción y una notificación
     * informativa a los demás administradores.
     *
     * @param dispositivo El [Dispositivo] que ha sido añadido.
     * @param user El [User] que realizó la acción de añadir el dispositivo.
     * @author Natalia González Álvarez
     */
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