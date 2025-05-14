package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketHandler
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.example.prestamoordenadores.config.websockets.models.NotificationSeverityDto
import org.example.prestamoordenadores.config.websockets.models.NotificationTypeDto
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.rest.prestamos.mappers.PrestamoMapper
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.storage.pdf.PrestamoPdfStorage
import org.example.prestamoordenadores.utils.emails.EmailService
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["prestamos"])
class PrestamoServiceImpl(
    private val prestamoRepository: PrestamoRepository,
    private val mapper: PrestamoMapper,
    private val userRepository: UserRepository,
    private val dispositivoRepository: DispositivoRepository,
    private val prestamoPdfStorage: PrestamoPdfStorage,
    private val webService : WebSocketService,
    private val emailService: EmailService
): PrestamoService {
    override fun getAllPrestamos(page: Int, size: Int): Result<PagedResponse<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo todos los prestamos" }

        val pageRequest = PageRequest.of(page, size)
        val pagePrestamos = prestamoRepository.findAll(pageRequest)
        val prestamoResponses = mapper.toPrestamoResponseList(pagePrestamos.content)

        val pagedResponse = PagedResponse(
            content = prestamoResponses,
            totalElements = pagePrestamos.totalElements
        )

        return Ok(pagedResponse)
    }

    @Cacheable(key = "#guid")
    override fun getPrestamoByGuid(guid: String): Result<PrestamoResponse?, PrestamoError> {
        logger.debug { "Obteniendo prestamo con GUID: $guid" }

        val prestamo = prestamoRepository.findByGuid(guid)

        return if (prestamo == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            Ok(mapper.toPrestamoResponse(prestamo))
        }
    }

    @CachePut(key = "#result.guid")
    override fun createPrestamo(): Result<PrestamoResponse, PrestamoError> {
        logger.debug { "Creando nuevo prestamo" }

        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name

        val user = userRepository.findByEmail(email)
            ?: return Err(PrestamoError.UserNotFound("No se encontró el usuario con email: $email"))

        val dispositivosDisponibles = dispositivoRepository.findByEstadoDispositivo(EstadoDispositivo.DISPONIBLE)
        if (dispositivosDisponibles.isEmpty()) {
            return Err(PrestamoError.DispositivoNotFound("No hay dispositivos disponibles actualmente"))
        }

        val dispositivoSeleccionado = dispositivosDisponibles.random()

        val prestamoCreado = mapper.toPrestamoFromCreate(user, dispositivoSeleccionado)
        prestamoCreado.fechaDevolucion = LocalDate.now().plusWeeks(3)
        prestamoRepository.save(prestamoCreado)

        dispositivoSeleccionado.estadoDispositivo = EstadoDispositivo.PRESTADO
        dispositivoRepository.save(dispositivoSeleccionado)

        prestamoPdfStorage.generateAndSavePdf(prestamoCreado.guid)

        enviarCorreo(user, dispositivoSeleccionado, prestamoCreado)

        sendNotificationNuevoPrestamo(prestamoCreado, user)
        return Ok(mapper.toPrestamoResponse(prestamoCreado))
    }

    @CachePut(key = "#result.guid")
    override fun updatePrestamo(guid: String, prestamo: PrestamoUpdateRequest): Result<PrestamoResponse?, PrestamoError> {
        logger.debug { "Actualizando prestamo con GUID: $guid" }

        val prestamoValidado = prestamo.validate()
        if (prestamoValidado.isErr) {
            return Err(PrestamoError.PrestamoValidationError("Préstamo inválido"))
        }

        val prestamoEncontrado = prestamoRepository.findByGuid(guid)

        return if (prestamoEncontrado == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            val nuevoEstadoPrestamo = EstadoPrestamo.valueOf(prestamo.estadoPrestamo.uppercase())

            if (nuevoEstadoPrestamo == EstadoPrestamo.DEVUELTO) {
                prestamoEncontrado.dispositivo.estadoDispositivo = EstadoDispositivo.DISPONIBLE
            }

            prestamoEncontrado.estadoPrestamo = nuevoEstadoPrestamo
            prestamoEncontrado.updatedDate = LocalDateTime.now()

            try {
                val prestamoGuardado = prestamoRepository.save(prestamoEncontrado)
                logger.info { "Préstamo GUID: ${prestamoGuardado?.guid} actualizado a estado: ${prestamoGuardado?.estadoPrestamo}" }

                sendNotificationActualizacionPrestamo(prestamoGuardado!!, prestamoGuardado?.estadoPrestamo?.name!!)
                Ok(mapper.toPrestamoResponse(prestamoGuardado))
            } catch (e: Exception) {
                logger.error { "Error al guardar el préstamo $guid: ${e.message}" }
                Err(PrestamoError.PrestamoValidationError("Error al guardar la actualización del préstamo"))
            }
        }
    }

    @CachePut(key = "#guid")
    override fun deletePrestamoByGuid(guid: String): Result<PrestamoResponse, PrestamoError> {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name

        val user = userRepository.findByEmail(email)
            ?: return Err(PrestamoError.UserNotFound("No se encontró el usuario con email: $email"))


        logger.debug { "Cancelando prestamo con GUID: $guid" }

        val prestamoEncontrado = prestamoRepository.findByGuid(guid)

        return if (prestamoEncontrado == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            prestamoEncontrado.estadoPrestamo = EstadoPrestamo.CANCELADO
            prestamoRepository.save(prestamoEncontrado)

            sendNotificationEliminacionPrestamo(prestamoEncontrado, user)
            Ok(mapper.toPrestamoResponse(prestamoEncontrado))
        }
    }

    @Cacheable(key = "#fechaPrestamo")
    override fun getByFechaPrestamo(fechaPrestamo: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de prestamo: $fechaPrestamo" }

        val prestamos = prestamoRepository.findByFechaPrestamo(fechaPrestamo)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    @Cacheable(key = "#fechaDevolucion")
    override fun getByFechaDevolucion(fechaDevolucion: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de devolucion: $fechaDevolucion" }

        val prestamos = prestamoRepository.findByFechaDevolucion(fechaDevolucion)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    @Cacheable(key = "#userGuid")
    override fun getPrestamoByUserGuid(userGuid: String): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos del usuario con GUID: $userGuid" }

        val user = userRepository.findByGuid(userGuid)
        if (user == null) {
            return Err(PrestamoError.UserNotFound("Usuario con GUID: $userGuid no encontrado"))
        }

        val prestamos = prestamoRepository.findByUserGuid(userGuid)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    private fun enviarCorreo(user: User, dispositivoSeleccionado: Dispositivo, prestamoCreado: Prestamo) {
        val pdfBytes = prestamoPdfStorage.generatePdf(prestamoCreado.guid)

        emailService.sendHtmlEmail(
            to = user.email,
            subject = "Confirmación de Préstamo",
            nombreUsuario = user.nombre,
            numeroSerieDispositivo = dispositivoSeleccionado.numeroSerie,
            fechaDevolucion = prestamoCreado.fechaDevolucion.toDefaultDateString(),
            pdfBytes = pdfBytes,
            nombreArchivoPdf = "prestamo_${prestamoCreado.fechaPrestamo.toDefaultDateString()}.pdf"
        )
    }

    private fun sendNotificationNuevoPrestamo(prestamo: Prestamo, user: User) {
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Solicitud de Préstamo Recibida: ${prestamo.guid}",
            mensaje = "Tu solicitud de préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' ha sido procesada.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.PRESTAMO,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.SUCCESS
        )
        webService.createAndSendNotification(user.email, notificacionParaUser)

        val administradores = userRepository.findUsersByRol(Role.ADMIN)

        administradores.forEach { admin ->
            if (admin?.email != user.email) {
                val notificacionParaAdmin = NotificationDto(
                    id = UUID.randomUUID().toString(),
                    titulo = "Nueva Solicitud de Préstamo: ${prestamo.guid}",
                    mensaje = "El usuario ${user.nombre} ${user.apellidos} ha solicitado un realizado un préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}'.",
                    fecha = LocalDateTime.now(),
                    leida = false,
                    tipo = NotificationTypeDto.PRESTAMO,
                    enlace = "/admin/prestamo/detalle/${prestamo.guid}",
                    severidadSugerida = NotificationSeverityDto.INFO
                )
                webService.createAndSendNotification(admin?.email ?: "", notificacionParaAdmin)
            }
        }
    }

    private fun sendNotificationActualizacionPrestamo(prestamo: Prestamo, tipoNotificacion: String) {
        var tituloNotificacion: String
        var mensajeNotificacion: String
        var severidad: NotificationSeverityDto

        when (tipoNotificacion) {
            "RECORDATORIO_CADUCIDAD" -> {
                tituloNotificacion = "Recordatorio: Tu Préstamo Caduca Pronto"
                mensajeNotificacion = "Recuerda que tu préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' caduca el ${prestamo.fechaDevolucion.toDefaultDateString()}."
                severidad = NotificationSeverityDto.WARNING
                logger.info { "Enviando RECORDATORIO de caducidad para préstamo GUID: ${prestamo.guid} a ${prestamo.user.email}" }
            }
            "VENCIDO" -> {
                tituloNotificacion = "¡Tu Préstamo Ha Caducado!"
                mensajeNotificacion = "El préstamo para '${prestamo.dispositivo.numeroSerie}' ha caducado hoy (${prestamo.fechaDevolucion.toDefaultDateString()}). Por favor, devuélvelo lo antes posible para evitar sanciones."
                severidad = NotificationSeverityDto.ERROR
                logger.info { "Enviando notificación de préstamo VENCIDO GUID: ${prestamo.guid} a ${prestamo.user.email}" }
            }
            "DEVUELTO" -> {
                tituloNotificacion = "Préstamo Devuelto"
                mensajeNotificacion = "El préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' ha sido devuelto."
                severidad = NotificationSeverityDto.SUCCESS
                logger.info { "Enviando notificación de préstamo DEVUELTO GUID: ${prestamo.guid} a ${prestamo.user.email}" }
            }
            "CANCELADO" -> {
                tituloNotificacion = "Préstamo Cancelado"
                mensajeNotificacion = "El préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' ha sido cancelado."
                severidad = NotificationSeverityDto.INFO
                logger.info { "Enviando notificación de préstamo CANCELADO GUID: ${prestamo.guid} a ${prestamo.user.email}" }
            }
            else -> {
                logger.warn { "Tipo de notificación de estado de préstamo desconocido: '$tipoNotificacion' para préstamo GUID: ${prestamo.guid}." }
                return
            }
        }

        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = tituloNotificacion,
            mensaje = mensajeNotificacion,
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SISTEMA,
            enlace = null,
            severidadSugerida = severidad
        )
        webService.createAndSendNotification(prestamo.user.email, notificacionParaUser)

        if (tipoNotificacion == "DEVUELTO" || tipoNotificacion == "CANCELADO") {
            val administradores = userRepository.findUsersByRol(Role.ADMIN)
            administradores.forEach { admin ->
                if (admin?.email != null) {
                    val notificacionParaAdmin = NotificationDto(
                        id = UUID.randomUUID().toString(),
                        titulo = tituloNotificacion,
                        mensaje = mensajeNotificacion,
                        fecha = LocalDateTime.now(),
                        leida = false,
                        tipo = NotificationTypeDto.SISTEMA,
                        enlace = "/admin/prestamo/detalle/${prestamo.guid}",
                        severidadSugerida = severidad
                    )
                    webService.createAndSendNotification(admin.email, notificacionParaAdmin)
                }
            }
        }
    }

    private fun sendNotificationEliminacionPrestamo(prestamo: Prestamo, user: User) {
        val notificacionAdminElimina = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Eliminaste Préstamo: ${prestamo.guid}",
            mensaje = "Has eliminado correctamente el préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' (solicitado por: ${prestamo.user.nombre} ${prestamo.user.apellidos}).",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SISTEMA,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.SUCCESS
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
                        titulo = "Préstamo Eliminado por: ${prestamo.guid}",
                        mensaje = "El préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' (solicitado por: ${prestamo.user.nombre} ${prestamo.user.apellidos}) fue eliminado por ${user.nombre} ${user.apellidos}.",
                        fecha = LocalDateTime.now(),
                        leida = false,
                        tipo = NotificationTypeDto.SISTEMA,
                        enlace = "/admin/prestamo/detalle/${prestamo.guid}",
                        severidadSugerida = NotificationSeverityDto.INFO
                    )
                    logger.debug { "Preparando notificación informativa de eliminaicon para otro admin (${otroAdmin.email}): $notificacionParaOtroAdmin" }
                    webService.createAndSendNotification(otroAdmin.email, notificacionParaOtroAdmin)
                }
            }
        }
    }

    //@Scheduled(cron = "0 0 2 * * *")
    @Scheduled(fixedRate = 60000)
    fun gestionarCaducidadPrestamos() {
        logger.info { "Iniciando tarea programada: Gestionar Caducidad y Recordatorios de Préstamos." }

        val prestamosQueCaducanHoy = prestamoRepository.findByFechaDevolucion(LocalDate.now())

        prestamosQueCaducanHoy.forEach { prestamo ->
            logger.info { "Préstamo GUID: ${prestamo.guid} caduca hoy. Cambiando estado a VENCIDO." }
            prestamo.estadoPrestamo = EstadoPrestamo.VENCIDO
            prestamo.updatedDate = LocalDateTime.now()
            val prestamoActualizado = prestamoRepository.save(prestamo)
            if (prestamoActualizado == null) {
                logger.error { "No se pudo actualizar el prestamo con GUID: ${prestamo.guid}" }
                return
            }

            sendNotificationActualizacionPrestamo(prestamoActualizado, "VENCIDO")
        }

        val fechaParaRecordatorio = LocalDate.now().plusDays(1)
        val prestamosParaRecordatorio = prestamoRepository.findByFechaDevolucion(fechaParaRecordatorio)

        prestamosParaRecordatorio.forEach { prestamo ->
            logger.info { "Préstamo GUID: ${prestamo.guid} caduca el $fechaParaRecordatorio. Enviando recordatorio." }
            sendNotificationActualizacionPrestamo(prestamo, "RECORDATORIO_CADUCIDAD")
        }

        logger.info { "Tarea programada: Gestionar Caducidad y Recordatorios de Préstamos finalizada." }
    }

    @CachePut(cacheNames = ["prestamos"], key = "#result.value.guid", condition = "#result.isOk && #result.value != null")
    @Transactional
    override fun cancelarPrestamo(guid: String): Result<PrestamoResponse?, PrestamoError> {
        logger.debug { "Cancelando préstamo con GUID: $guid" }

        val prestamoEncontrado = prestamoRepository.findByGuid(guid)

        return if (prestamoEncontrado == null) {
            logger.warn { "Préstamo con GUID: $guid no encontrado para cancelar." }
            Err(PrestamoError.PrestamoNotFound("Préstamo con GUID: $guid no encontrado"))
        } else {
            if (prestamoEncontrado.estadoPrestamo == EstadoPrestamo.CANCELADO || prestamoEncontrado.estadoPrestamo == EstadoPrestamo.DEVUELTO) {
                logger.info { "El préstamo GUID: $guid ya está en estado ${prestamoEncontrado.estadoPrestamo}. No se requiere acción." }
                prestamoEncontrado.dispositivo.estadoDispositivo = EstadoDispositivo.DISPONIBLE
                return Ok(mapper.toPrestamoResponse(prestamoEncontrado))
            }

            prestamoEncontrado.estadoPrestamo = EstadoPrestamo.CANCELADO
            prestamoEncontrado.updatedDate = LocalDateTime.now()

            try {
                val prestamoGuardado = prestamoRepository.save(prestamoEncontrado)
                if (prestamoGuardado == null) {
                    return Err(PrestamoError.PrestamoValidationError("Error al guardar la cancelación del préstamo"))
                }
                logger.info { "Préstamo GUID: ${prestamoGuardado.guid} actualizado a estado: CANCELADO" }

                sendNotificationActualizacionPrestamo(prestamoGuardado, "CANCELADO")
                Ok(mapper.toPrestamoResponse(prestamoGuardado))
            } catch (e: Exception) {
                logger.error { "Error al guardar el préstamo $guid durante la cancelación: ${e.message}"}
                Err(PrestamoError.PrestamoValidationError("Error al guardar la cancelación del préstamo"))
            }
        }
    }
}