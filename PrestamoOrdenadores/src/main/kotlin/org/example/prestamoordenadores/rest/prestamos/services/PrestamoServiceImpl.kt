package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.example.prestamoordenadores.config.websockets.models.NotificationSeverityDto
import org.example.prestamoordenadores.config.websockets.models.NotificationTypeDto
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
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
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
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

/**
 * Implementación del servicio de gestión de préstamos.
 *
 * Proporciona la lógica de negocio para las operaciones CRUD y consultas relacionadas
 * con los préstamos, incluyendo la interacción con la base de datos, mapeo de DTOs,
 * manejo de errores, notificaciones vía WebSocket y gestión de emails.
 * También incluye tareas programadas para la gestión de la caducidad de los préstamos.
 *
 * @property prestamoRepository Repositorio para operaciones de base de datos de [Prestamo].
 * @property mapper Mapeador para convertir entre entidades [Prestamo] y DTOs.
 * @property userRepository Repositorio para operaciones de base de datos de [User].
 * @property dispositivoRepository Repositorio para operaciones de base de datos de [Dispositivo].
 * @property prestamoPdfStorage Servicio para la generación y almacenamiento de PDFs de préstamos.
 * @property webService Servicio para el envío de notificaciones WebSocket.
 * @property emailService Servicio para el envío de correos electrónicos.
 * @author Natalia González Álvarez
 */
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

    /**
     * Obtiene una lista paginada de todos los préstamos.
     *
     * @param page El número de página (basado en 0).
     * @param size El tamaño de la página.
     * @return Un [Result.Ok] con un [PagedResponse] de [PrestamoResponse] si es exitoso.
     * @author Natalia González Álvarez
     */
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

    /**
     * Obtiene un préstamo por su identificador único global (GUID) para una vista administrativa.
     *
     * Este método está cacheado.
     *
     * @param guid El GUID del préstamo a buscar.
     * @return Un [Result.Ok] con el [PrestamoResponseAdmin] si se encuentra, o un [Result.Err] con [PrestamoError.PrestamoNotFound] si no.
     * @author Natalia González Álvarez
     */
    @Cacheable(key = "#guid")
    override fun getPrestamoByGuid(guid: String): Result<PrestamoResponseAdmin?, PrestamoError> {
        logger.debug { "Obteniendo prestamo con GUID: $guid" }

        val prestamo = prestamoRepository.findByGuid(guid)

        return if (prestamo == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            Ok(mapper.toPrestamoResponseAdmin(prestamo))
        }
    }

    /**
     * Crea un nuevo préstamo.
     *
     * Obtiene el usuario autenticado y un dispositivo disponible aleatoriamente.
     * Marca el dispositivo como "PRESTADO" y guarda el nuevo préstamo.
     * Genera y guarda un PDF del préstamo y envía correos electrónicos y notificaciones.
     * Este método actualiza la caché tras la creación.
     *
     * @return Un [Result.Ok] con el [PrestamoResponse] del préstamo creado si es exitoso,
     * o un [Result.Err] con un [PrestamoError] si falla (ej. usuario no encontrado, no hay dispositivos disponibles).
     * @author Natalia González Álvarez
     */
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

    /**
     * Actualiza un préstamo existente identificado por su GUID.
     *
     * Valida la solicitud de actualización y actualiza el estado del préstamo.
     * Si el nuevo estado es "DEVUELTO", el dispositivo asociado cambia a "DISPONIBLE".
     * Envía notificaciones tras la actualización.
     * Este método actualiza la caché tras la actualización.
     *
     * @param guid El GUID del préstamo a actualizar.
     * @param prestamo La solicitud [PrestamoUpdateRequest] con el nuevo estado.
     * @return Un [Result.Ok] con el [PrestamoResponse] actualizado si es exitoso,
     * o un [Result.Err] con [PrestamoError.PrestamoNotFound] si no se encuentra el préstamo,
     * o [PrestamoError.PrestamoValidationError] si la validación falla o hay un error al guardar.
     * @author Natalia González Álvarez
     */
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
            val nuevoEstadoPrestamo = try {
                EstadoPrestamo.valueOf(prestamo.estadoPrestamo.uppercase())
            } catch (e: IllegalArgumentException) {
                return Err(PrestamoError.PrestamoValidationError("Estado de préstamo inválido: ${prestamo.estadoPrestamo}"))
            }


            if (nuevoEstadoPrestamo == EstadoPrestamo.DEVUELTO) {
                prestamoEncontrado.dispositivo.estadoDispositivo = EstadoDispositivo.DISPONIBLE
                dispositivoRepository.save(prestamoEncontrado.dispositivo) // Asegura que el dispositivo se actualice
            }

            prestamoEncontrado.estadoPrestamo = nuevoEstadoPrestamo
            prestamoEncontrado.updatedDate = LocalDateTime.now()

            try {
                val prestamoGuardado = prestamoRepository.save(prestamoEncontrado)
                logger.info { "Préstamo GUID: ${prestamoGuardado?.guid} actualizado a estado: ${prestamoGuardado?.estadoPrestamo}" }

                // Asegúrate de que prestamoGuardado no sea nulo antes de llamar a sendNotificationActualizacionPrestamo
                prestamoGuardado?.let {
                    sendNotificationActualizacionPrestamo(it, it.estadoPrestamo.name)
                    Ok(mapper.toPrestamoResponse(it))
                } ?: Err(PrestamoError.PrestamoValidationError("Error al guardar la actualización del préstamo: el préstamo guardado es nulo."))
            } catch (e: Exception) {
                logger.error { "Error al guardar el préstamo $guid: ${e.message}" }
                Err(PrestamoError.PrestamoValidationError("Error al guardar la actualización del préstamo"))
            }
        }
    }

    /**
     * Elimina lógicamente un préstamo por su GUID, marcándolo como eliminado.
     *
     * Requiere que un administrador esté autenticado. Actualiza el estado de la incidencia
     * a `isDeleted = true` y su fecha de actualización. Envía notificaciones a los administradores.
     * Este método invalida la entrada en caché correspondiente al GUID.
     *
     * @param guid El GUID del préstamo a eliminar lógicamente.
     * @return Un [Result.Ok] con el [PrestamoResponseAdmin] del dispositivo modificado si es exitoso,
     * o un [Result.Err] con [PrestamoError.PrestamoNotFound] si el préstamo no se encuentra,
     * o [PrestamoError.UserNotFound] si el administrador no es encontrado.
     * @author Natalia González Álvarez
     */
    @CacheEvict
    override fun deletePrestamoByGuid(guid: String): Result<PrestamoResponseAdmin, PrestamoError> {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name

        val user = userRepository.findByEmail(email)
            ?: return Err(PrestamoError.UserNotFound("No se encontró el usuario con email: $email"))


        logger.debug { "Cancelando prestamo con GUID: $guid" }

        val prestamoEncontrado = prestamoRepository.findByGuid(guid)
        if (prestamoEncontrado == null) {
            return Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        }

        if (prestamoEncontrado.estadoPrestamo == EstadoPrestamo.EN_CURSO || prestamoEncontrado.estadoPrestamo == EstadoPrestamo.VENCIDO) {
            return Err(PrestamoError.PrestamoValidationError("No se puede eliminar un préstamo que está EN_CURSO o VENCIDO."))
        }

        prestamoEncontrado.isDeleted = true
        prestamoEncontrado.updatedDate = LocalDateTime.now()

        prestamoRepository.save(prestamoEncontrado)

        sendNotificationEliminacionPrestamo(prestamoEncontrado, user)
        return Ok(mapper.toPrestamoResponseAdmin(prestamoEncontrado))
    }

    /**
     * Obtiene una lista de préstamos por su fecha de inicio.
     *
     * Este método está cacheado.
     *
     * @param fechaPrestamo La fecha de inicio de los préstamos a buscar.
     * @return Un [Result.Ok] con una lista de [PrestamoResponse] si es exitoso.
     * @author Natalia González Álvarez
     */
    @Cacheable(key = "#fechaPrestamo")
    override fun getByFechaPrestamo(fechaPrestamo: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de prestamo: $fechaPrestamo" }

        val prestamos = prestamoRepository.findByFechaPrestamo(fechaPrestamo)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    /**
     * Obtiene una lista de préstamos por su fecha de devolución.
     *
     * Este método está cacheado.
     *
     * @param fechaDevolucion La fecha de devolución de los préstamos a buscar.
     * @return Un [Result.Ok] con una lista de [PrestamoResponse] si es exitoso.
     * @author Natalia González Álvarez
     */
    @Cacheable(key = "#fechaDevolucion")
    override fun getByFechaDevolucion(fechaDevolucion: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de devolucion: $fechaDevolucion" }

        val prestamos = prestamoRepository.findByFechaDevolucion(fechaDevolucion)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    /**
     * Obtiene una lista de préstamos asociados a un usuario específico por su GUID.
     *
     * Este método está cacheado.
     *
     * @param userGuid El GUID del usuario cuyas préstamos se desean buscar.
     * @return Un [Result.Ok] con una lista de [PrestamoResponse] si es exitoso,
     * o un [Result.Err] con [PrestamoError.UserNotFound] si el usuario no es encontrado.
     * @author Natalia González Álvarez
     */
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

    /**
     * Envía un correo electrónico de confirmación de préstamo.
     *
     * Incluye los detalles del préstamo y un PDF adjunto.
     *
     * @param user El [User] que realizó el préstamo.
     * @param dispositivoSeleccionado El [Dispositivo] prestado.
     * @param prestamoCreado El [Prestamo] creado.
     * @author Natalia González Álvarez
     */
    private fun enviarCorreo(user: User, dispositivoSeleccionado: Dispositivo, prestamoCreado: Prestamo) {
        val pdfBytes = prestamoPdfStorage.generatePdf(prestamoCreado.guid) // Genera los bytes del PDF

        emailService.sendHtmlEmailPrestamoCreado(
            to = user.email,
            subject = "Confirmación de Préstamo",
            nombreUsuario = user.nombre,
            numeroSerieDispositivo = dispositivoSeleccionado.numeroSerie,
            fechaDevolucion = prestamoCreado.fechaDevolucion.toDefaultDateString(),
            pdfBytes = pdfBytes,
            nombreArchivoPdf = "prestamo_${prestamoCreado.fechaPrestamo.toDefaultDateString()}.pdf"
        )
    }

    /**
     * Envía notificaciones WebSocket cuando se crea un nuevo préstamo.
     *
     * Notifica al usuario que realizó el préstamo y a todos los administradores.
     *
     * @param prestamo El [Prestamo] que ha sido creado.
     * @param user El [User] que realizó el préstamo.
     * @author Natalia González Álvarez
     */
    private fun sendNotificationNuevoPrestamo(prestamo: Prestamo, user: User) {
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Solicitud de Préstamo Recibida: ${prestamo.guid}",
            mensaje = "Tu solicitud de préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' ha sido procesada.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.PRESTAMO,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.SUCCESS,
            mostrarToast = true
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
                    severidadSugerida = NotificationSeverityDto.INFO,
                    mostrarToast = false
                )
                webService.createAndSendNotification(admin?.email ?: "", notificacionParaAdmin)
            }
        }
    }

    /**
     * Envía notificaciones WebSocket cuando el estado de un préstamo se actualiza.
     *
     * Adapta el título, mensaje y severidad de la notificación según el tipo de actualización (recordatorio, vencido, devuelto, cancelado).
     *
     * @param prestamo El [Prestamo] que ha sido actualizado.
     * @param tipoNotificacion La cadena que describe el tipo de actualización (ej. "VENCIDO", "DEVUELTO").
     * @author Natalia González Álvarez
     */
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
            severidadSugerida = severidad,
            mostrarToast = true
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
                        severidadSugerida = severidad,
                        mostrarToast = false
                    )
                    webService.createAndSendNotification(admin.email, notificacionParaAdmin)
                }
            }
        }
    }

    /**
     * Envía notificaciones WebSocket cuando se elimina lógicamente un préstamo.
     *
     * Notifica al administrador que realizó la eliminación y a los demás administradores.
     *
     * @param prestamo El [Prestamo] que ha sido eliminado lógicamente.
     * @param user El [User] (administrador) que realizó la eliminación.
     * @author Natalia González Álvarez
     */
    private fun sendNotificationEliminacionPrestamo(prestamo: Prestamo, user: User) {
        val notificacionAdminElimina = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Eliminaste Préstamo: ${prestamo.guid}",
            mensaje = "Has eliminado correctamente el préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' (solicitado por: ${prestamo.user.nombre} ${prestamo.user.apellidos}).",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SISTEMA,
            enlace = null,
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
                        titulo = "Préstamo Eliminado por: ${prestamo.guid}",
                        mensaje = "El préstamo para el dispositivo '${prestamo.dispositivo.numeroSerie}' (solicitado por: ${prestamo.user.nombre} ${prestamo.user.apellidos}) fue eliminado por ${user.nombre} ${user.apellidos}.",
                        fecha = LocalDateTime.now(),
                        leida = false,
                        tipo = NotificationTypeDto.SISTEMA,
                        enlace = "/admin/prestamo/detalle/${prestamo.guid}",
                        severidadSugerida = NotificationSeverityDto.INFO,
                        mostrarToast = false
                    )
                    logger.debug { "Preparando notificación informativa de eliminaicon para otro admin (${otroAdmin.email}): $notificacionParaOtroAdmin" }
                    webService.createAndSendNotification(otroAdmin.email, notificacionParaOtroAdmin)
                }
            }
        }
    }

    /**
     * Tarea programada que gestiona la caducidad de los préstamos.
     *
     * Se ejecuta diariamente a las 2 AM (0 0 2 * * *).
     * Identifica los préstamos que caducan hoy y los marca como "VENCIDO".
     * También identifica los préstamos que caducarán mañana y envía recordatorios.
     * Envía correos electrónicos y notificaciones WebSocket correspondientes.
     *
     * @author Natalia González Álvarez
     */
    @Scheduled(cron = "0 0 2 * * *")
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
                return@forEach
            }

            enviarCorreoPrestamoCaducado(prestamo.user, prestamo.dispositivo, prestamoActualizado)
            sendNotificationActualizacionPrestamo(prestamoActualizado, "VENCIDO")
        }

        val fechaParaRecordatorio = LocalDate.now().plusDays(1)
        val prestamosParaRecordatorio = prestamoRepository.findByFechaDevolucion(fechaParaRecordatorio)

        prestamosParaRecordatorio.forEach { prestamo ->
            logger.info { "Préstamo GUID: ${prestamo.guid} caduca el $fechaParaRecordatorio. Enviando recordatorio." }
            enviarCorreoPrestamoApuntodeCaducar(prestamo.user, prestamo.dispositivo, prestamo)
            sendNotificationActualizacionPrestamo(prestamo, "RECORDATORIO_CADUCIDAD")
        }

        logger.info { "Tarea programada: Gestionar Caducidad y Recordatorios de Préstamos finalizada." }
    }

    /**
     * Cancela un préstamo específico por su GUID.
     *
     * Si el préstamo ya está cancelado o devuelto, no realiza cambios.
     * Si no, cambia el estado del préstamo a "CANCELADO" y su fecha de actualización.
     * Envía notificaciones tras la cancelación.
     * Este método actualiza la caché tras la cancelación.
     *
     * @param guid El GUID del préstamo a cancelar.
     * @return Un [Result.Ok] con el [PrestamoResponse] del préstamo cancelado si es exitoso,
     * o un [Result.Err] con [PrestamoError.PrestamoNotFound] si el préstamo no se encuentra,
     * o [PrestamoError.PrestamoValidationError] si hay un error al guardar.
     * @author Natalia González Álvarez
     */
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
                if(prestamoEncontrado.dispositivo.estadoDispositivo != EstadoDispositivo.DISPONIBLE){
                    prestamoEncontrado.dispositivo.estadoDispositivo = EstadoDispositivo.DISPONIBLE
                    dispositivoRepository.save(prestamoEncontrado.dispositivo)
                }
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

    /**
     * Envía un correo electrónico de recordatorio de préstamo a punto de caducar.
     *
     * @param user El [User] del préstamo.
     * @param dispositivo El [Dispositivo] prestado.
     * @param prestamo El [Prestamo] correspondiente.
     * @author Natalia González Álvarez
     */
    private fun enviarCorreoPrestamoApuntodeCaducar(user: User, dispositivo: Dispositivo, prestamo: Prestamo) {
        emailService.sendHtmlEmailPrestamoApuntodeCaducar(
            to = user.email,
            subject = "Recordatorio: Tu préstamo está a punto de caducar",
            nombreUsuario = user.nombre,
            numeroSerieDispositivo = dispositivo.numeroSerie,
            fechaDevolucion = prestamo.fechaDevolucion.toDefaultDateString()
        )
    }

    /**
     * Envía un correo electrónico de notificación de préstamo caducado.
     *
     * @param user El [User] del préstamo.
     * @param dispositivo El [Dispositivo] prestado.
     * @param prestamo El [Prestamo] correspondiente.
     * @author Natalia González Álvarez
     */
    private fun enviarCorreoPrestamoCaducado(user: User, dispositivo: Dispositivo, prestamo: Prestamo) {
        emailService.sendHtmlEmailPrestamoCaducado(
            to = user.email,
            subject = "¡Urgente! Tu préstamo ha caducado",
            nombreUsuario = user.nombre,
            numeroSerieDispositivo = dispositivo.numeroSerie,
            fechaCaducidad = prestamo.fechaDevolucion.toDefaultDateString()
        )
    }
}