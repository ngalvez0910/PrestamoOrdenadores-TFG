package org.example.prestamoordenadores.rest.sanciones.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.config.websockets.models.NotificationDto
import org.example.prestamoordenadores.config.websockets.models.NotificationSeverityDto
import org.example.prestamoordenadores.config.websockets.models.NotificationTypeDto
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.sanciones.dto.SancionAdminResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.example.prestamoordenadores.rest.sanciones.mappers.SancionMapper
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.emails.EmailService
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

private val logger = logging()

/**
 * Implementación del servicio de gestión de sanciones.
 * Proporciona operaciones CRUD para las sanciones, así como lógica de negocio
 * para la gestión automática de advertencias y bloqueos de usuarios.
 * Utiliza caché para optimizar el acceso a datos y WebSockets para notificaciones en tiempo real.
 *
 * @property repository Repositorio para el acceso a datos de sanciones.
 * @property mapper Mapper para convertir entre entidades Sancion y DTOs.
 * @property userRepository Repositorio para el acceso a datos de usuarios.
 * @property prestamoRepository Repositorio para el acceso a datos de préstamos.
 * @property webService Servicio para el envío de notificaciones WebSocket.
 * @property emailService Servicio para el envío de correos electrónicos.
 *
 * @author Natalia González Álvarez
 */
@Service
@CacheConfig(cacheNames = ["sanciones"])
class SancionServiceImpl(
    private val repository : SancionRepository,
    private val mapper: SancionMapper,
    private val userRepository: UserRepository,
    private val prestamoRepository: PrestamoRepository,
    private val webService : WebSocketService,
    private val emailService: EmailService
) : SancionService {
    /**
     * Obtiene todas las sanciones paginadas.
     *
     * @param page Número de página (0-indexed).
     * @param size Tamaño de la página.
     * @return [Result] que contiene un [PagedResponse] de [SancionResponse] si la operación es exitosa, o un [SancionError] en caso contrario.
     */
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

    /**
     * Obtiene una sanción por su GUID.
     *
     * @param guid El GUID de la sanción a buscar.
     * @return [Result] que contiene un [SancionResponse] si la sanción es encontrada, o un [SancionError.SancionNotFound] en caso contrario.
     */
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

    /**
     * Obtiene una sanción por su GUID para la vista de administrador, incluyendo más detalles.
     *
     * @param guid El GUID de la sanción a buscar.
     * @return [Result] que contiene un [SancionAdminResponse] si la sanción es encontrada, o un [SancionError.SancionNotFound] en caso contrario.
     */
    @Cacheable(key = "#guid")
    override fun getSancionByGuidAdmin(guid: String): Result<SancionAdminResponse?, SancionError> {
        logger.debug { "Obteniendo sancion con GUID: $guid" }

        val sancion = repository.findByGuid(guid)

        return if (sancion == null) {
            Err(SancionError.SancionNotFound(guid))
        } else {
            Ok(mapper.toSancionAdminResponse(sancion))
        }
    }

    /**
     * Actualiza una sanción existente.
     *
     * @param guid El GUID de la sanción a actualizar.
     * @param sancionUpdateDto DTO con los datos de actualización de la sanción.
     * @return [Result] que contiene un [SancionResponse] de la sanción actualizada si la operación es exitosa,
     * o un [SancionError] en caso de error de validación o si la sanción no es encontrada.
     */
    @CachePut(key = "#guid")
    @Transactional
    override fun updateSancion(guid: String, sancionUpdateDto: SancionUpdateRequest): Result<SancionResponse?, SancionError> {
        logger.info { "[ADMIN] Actualizando sanción con GUID: $guid" }

        if (sancionUpdateDto.validate().isErr) { return Err(SancionError.SancionValidationError("Error en la validacion")) }

        val existingSancion = repository.findByGuid(guid)
            ?: return Err(SancionError.SancionNotFound("Sancion con GUID $guid no encontrada para actualizar."))

        var updated = false
        sancionUpdateDto.tipoSancion.let { tipoStr ->
            try {
                val nuevoTipo = TipoSancion.valueOf(tipoStr.replace(" ", "_").uppercase())
                if (existingSancion.tipoSancion != nuevoTipo) {
                    logger.info { "Admin cambiando tipo de sanción $guid de ${existingSancion.tipoSancion} a $nuevoTipo" }
                    existingSancion.tipoSancion = nuevoTipo
                    updated = true
                }
            } catch (e: IllegalArgumentException) {
                return Err(SancionError.SancionValidationError("Tipo de sanción no válido: $tipoStr"))
            }
        }

        return if (updated) {
            existingSancion.updatedDate = LocalDateTime.now()
            val sancionGuardada = repository.save(existingSancion)
            sendNotificationSancionModificada(sancionGuardada)
            Ok(mapper.toSancionResponse(sancionGuardada))
        } else {
            logger.info { "No se realizaron cambios en la sanción $guid."}
            Ok(mapper.toSancionResponse(existingSancion))
        }
    }

    /**
     * Elimina (marca como eliminada) una sanción por su GUID.
     *
     * @param guid El GUID de la sanción a eliminar.
     * @return [Result] que contiene un [SancionAdminResponse] de la sanción eliminada si la operación es exitosa,
     * o un [SancionError.SancionNotFound] si la sanción no es encontrada.
     */
    @CacheEvict
    override fun deleteSancionByGuid(guid: String): Result<SancionAdminResponse?, SancionError> {
        logger.debug { "Buscando sancion" }

        val existingSancion = repository.findByGuid(guid)
        if (existingSancion == null) {
            return Err(SancionError.SancionNotFound("Sancion no encontrada"))
        }

        logger.debug { "Eliminando sancion" }
        existingSancion.isDeleted = true
        existingSancion.updatedDate = LocalDateTime.now()

        repository.save(existingSancion)

        sendNotificationSancionEliminada(existingSancion)
        return Ok(mapper.toSancionAdminResponse(existingSancion))
    }

    /**
     * Obtiene una lista de sanciones por su fecha de sanción.
     *
     * @param fecha La fecha de sanción a buscar.
     * @return [Result] que contiene una lista de [SancionResponse] si la operación es exitosa.
     */
    @Cacheable(key = "#fecha")
    override fun getByFecha(fecha: LocalDate): Result<List<SancionResponse>, SancionError> {
        logger.debug { "Obteniendo sanciones con fecha: $fecha" }

        val sanciones = repository.findSancionByFechaSancion(fecha)
        return Ok(mapper.toSancionResponseList(sanciones))
    }

    /**
     * Obtiene una lista de sanciones por su tipo de sanción.
     *
     * @param tipo El tipo de sanción a buscar (String, se normalizará a [TipoSancion]).
     * @return [Result] que contiene una lista de [SancionResponse] si la operación es exitosa,
     * o un [SancionError.SancionNotFound] si el tipo de sanción no es válido.
     */
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

    /**
     * Obtiene una lista de sanciones asociadas a un usuario específico por su GUID.
     *
     * @param userGuid El GUID del usuario.
     * @return [Result] que contiene una lista de [SancionResponse] si la operación es exitosa,
     * o un [SancionError.UserNotFound] si el usuario no es encontrado.
     */
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

    /**
     * Tarea programada que gestiona las advertencias por préstamos vencidos.
     * Se ejecuta cada 2 horas (a los 0 minutos de cada hora par).
     * Identifica préstamos en estado [EstadoPrestamo.VENCIDO] que han pasado más de 3 días en este estado
     * y genera una sanción de [TipoSancion.ADVERTENCIA] si no existe una ya para ese préstamo.
     * También evalúa si el usuario debe pasar a [TipoSancion.BLOQUEO_TEMPORAL].
     */
    @Scheduled(cron = "0 0 0/2 * * *")
    @Transactional
    fun gestionarAdvertencias() {
        logger.info { "Iniciando tarea programada: Gestionar Advertencias por Préstamos Vencidos (Lógica revisada según aclaración)." }

        val ahora = LocalDateTime.now()

        val prestamosVencidos = prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO)

        if (prestamosVencidos.isEmpty()) {
            logger.info { "No se encontraron préstamos en estado VENCIDO. Finalizando tarea de advertencias." }
            return
        }
        logger.info { "Se encontraron ${prestamosVencidos.size} préstamos en estado VENCIDO. Procesando para advertencias..." }


        prestamosVencidos.forEach { prestamo ->
            val fechaQuePasoAVencido: LocalDateTime = prestamo.updatedDate
            val fechaLimiteSinAdvertencia = fechaQuePasoAVencido.plusDays(3)

            if (ahora.isAfter(fechaLimiteSinAdvertencia)) {
                val user = prestamo.user
                val existeAdvertencia = repository.existsByPrestamoGuidAndTipoSancion(prestamo.guid, TipoSancion.ADVERTENCIA)

                if (!existeAdvertencia) {
                    logger.info {
                        "Generando sanción de ADVERTENCIA para usuario ${user.username} por préstamo ${prestamo.guid}. " +
                                "Pasó a VENCIDO el ${fechaQuePasoAVencido}, su límite para devolver sin advertencia era ${fechaLimiteSinAdvertencia}. " +
                                "Comprobación realizada el ${ahora}."
                    }
                    val nuevaAdvertencia = Sancion(
                        user = user,
                        tipoSancion = TipoSancion.ADVERTENCIA,
                        prestamo = prestamo
                    )
                    val sancionGuardada = repository.save(nuevaAdvertencia)

                    enviarCorreo(user, "ADVERTENCIA")

                    sendNotificationNuevaSancion(
                        sancionGuardada,
                        "automática por préstamo ${prestamo.guid} no devuelto tras 3 días en estado VENCIDO (marcado como VENCIDO el ${fechaQuePasoAVencido.toLocalDate()})"
                    )

                    evaluarPasoABloqueo(user)
                }
            }
        }
        logger.info { "Tarea programada: Gestionar Advertencias por Préstamos Vencidos (Lógica revisada según aclaración) finalizada." }
    }

    /**
     * Tarea programada que gestiona la reactivación de usuarios y la posible escalada a bloqueo indefinido.
     * Se ejecuta cada 2 horas, 5 minutos después de la hora (ej., 00:05, 02:05, etc.).
     * Reactiva a los usuarios cuyas sanciones de [TipoSancion.BLOQUEO_TEMPORAL] han expirado.
     * También evalúa si un usuario inactivo con sanciones expiradas puede ser reactivado.
     * Finalmente, evalúa si un usuario debe pasar a [TipoSancion.INDEFINIDO].
     */
    @Scheduled(cron = "0 5 0/2 * * *")
    @Transactional
    fun gestionarReactivacionYPosibleEscaladaAIndefinido() {
        logger.info { "Iniciando tarea programada: Gestionar Reactivación de Usuarios y posible Escalada a Indefinido." }
        val hoy = LocalDate.now()

        val sancionesBloqueo = repository.findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(
            TipoSancion.BLOQUEO_TEMPORAL, hoy
        )

        if (sancionesBloqueo.isEmpty()) {
            logger.info { "No se encontraron sanciones de BLOQUEO_TEMPORAL expiradas para usuarios inactivos. Finalizando tarea." }
            return
        }
        logger.info { "Se encontraron ${sancionesBloqueo.size} sanciones de BLOQUEO_TEMPORAL expiradas. Procesando..." }

        sancionesBloqueo.forEach { sancionBloqueo ->
            val user = sancionBloqueo.user

            if (!user.isActivo) {
                val sancionesBloqueantes = repository.findSancionsByUserAndTipoSancionIn(
                    user, listOf(TipoSancion.BLOQUEO_TEMPORAL, TipoSancion.INDEFINIDO)
                ).any { otraSancion ->
                    otraSancion.guid != sancionBloqueo.guid && otraSancion.isActiveNow()
                }

                if (!sancionesBloqueantes) {
                    user.isActivo = true
                    user.updatedDate = LocalDateTime.now()
                    userRepository.save(user)
                    logger.info { "Usuario ${user.username} reactivado. Sanción de bloqueo ${sancionBloqueo.guid} finalizada." }
                    enviarCorreoReactivacion(user)
                    sendNotificationReactivacionUsuario(user, sancionBloqueo, "finalización de bloqueo temporal")
                } else {
                    logger.info { "Usuario ${user.username} no reactivado (sanción ${sancionBloqueo.guid} expirada) debido a otras sanciones bloqueantes activas." }
                }
            }

            evaluarPasoAIndefinido(user)
        }
        logger.info { "Tarea programada: Gestionar Reactivación y Escalada a Indefinido finalizada." }
    }

    /**
     * Evalúa si un usuario debe ser sancionado con un [TipoSancion.INDEFINIDO].
     * Un usuario es sancionado indefinidamente si ha acumulado dos o más sanciones de [TipoSancion.BLOQUEO_TEMPORAL] cumplidas.
     *
     * @param user El usuario a evaluar.
     */
    @Transactional
    internal fun evaluarPasoAIndefinido(user: User) {
        logger.debug { "Evaluando escalada a INDEFINIDO para usuario ${user.username}." }

        val indefinidoActivo = repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO)
            .any { it.isActiveNow() }

        if (indefinidoActivo) {
            logger.debug { "Usuario ${user.username} ya tiene una sanción INDEFINIDA activa. No se evalúa más." }
            return
        }

        val bloqueosTemporalesCumplidos = repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL)
            .filter { sancion ->
                !sancion.fechaFin?.isAfter(LocalDate.now())!!
            }

        if (bloqueosTemporalesCumplidos.size >= 2) {
            val indefinidaRecienteCreadaHoy = repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO)
                .any { it.fechaSancion.isEqual(LocalDate.now()) }

            if (!indefinidaRecienteCreadaHoy) {
                logger.info { "Usuario ${user.username} ha acumulado ${bloqueosTemporalesCumplidos.size} bloqueos temporales cumplidos. Escalando a INDEFINIDO." }
                crearBloqueoIndefinido(user, "acumulación de ${bloqueosTemporalesCumplidos.size} bloqueos temporales (cumplidos)")
            } else {
                logger.info { "Usuario ${user.username} cumple para INDEFINIDO pero ya se creó una hoy. Evitando duplicado." }
            }
        } else {
            logger.debug { "Usuario ${user.username} tiene ${bloqueosTemporalesCumplidos.size} bloqueos temporales cumplidos. No se escala a INDEFINIDO aún." }
        }
    }

    /**
     * Evalúa si un usuario debe ser sancionado con un [TipoSancion.BLOQUEO_TEMPORAL].
     * Un usuario es sancionado temporalmente si ha acumulado dos o más sanciones de [TipoSancion.ADVERTENCIA].
     *
     * @param user El usuario a evaluar.
     */
    @Transactional
    internal fun evaluarPasoABloqueo(user: User) {
        logger.debug { "Evaluando escalada de ADVERTENCIA a BLOQUEO_TEMPORAL para usuario ${user.username}." }

        val bloqueoTemporalActivo = repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL)
            .any { it.isActiveNow() }

        if (bloqueoTemporalActivo) {
            logger.debug { "Usuario ${user.username} ya tiene un BLOQUEO_TEMPORAL activo. No se crea otro por advertencias." }
            return
        }

        val advertencias = repository.findByUserAndTipoSancion(user, TipoSancion.ADVERTENCIA).sortedByDescending { it.fechaSancion }

        if (advertencias.size >= 2) {
            val ultimaAdvertenciaRelevante = advertencias.firstOrNull { it.prestamo != null }
            val prestamo = ultimaAdvertenciaRelevante?.prestamo

            if (prestamo == null) {
                logger.error { "CRÍTICO: No se pudo determinar un préstamo para asociar al BLOQUEO_TEMPORAL del usuario ${user.username} y prestamo_id es NOT NULL. Abortando creación de bloqueo." }
                return
            }

            val bloqueoRecienteCreadoHoy = repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL)
                .any { it.fechaSancion.isEqual(LocalDate.now()) }

            if (!bloqueoRecienteCreadoHoy) {
                logger.info { "Usuario ${user.username} tiene ${advertencias.size} advertencias. Creando BLOQUEO_TEMPORAL." }
                crearBloqueoTemporal(user, "acumulación de ${advertencias.size} advertencias", prestamo)
            } else {
                logger.info { "Usuario ${user.username} cumple para BLOQUEO por advertencias pero ya se creó uno hoy. Evitando duplicado." }
            }
        } else {
            logger.debug { "Usuario ${user.username} tiene ${advertencias.size} advertencias. No se escala a BLOQUEO_TEMPORAL aún." }
        }
    }

    /**
     * Crea una sanción de [TipoSancion.BLOQUEO_TEMPORAL] para un usuario.
     * También inactiva al usuario si este está activo.
     *
     * @param user El usuario a sancionar.
     * @param motivo El motivo de la sanción.
     * @param prestamo El préstamo asociado a la sanción.
     */
    @Transactional
    internal fun crearBloqueoTemporal(user: User, motivo: String, prestamo: Prestamo) {
        logger.info { "Creando sanción BLOQUEO_TEMPORAL automática para usuario ${user.username}. Motivo: $motivo" }
        if (user.isActivo) {
            user.isActivo = false
            user.updatedDate = LocalDateTime.now()
            userRepository.save(user)
        } else {
            logger.warn { "Usuario ${user.username} ya está inactivo. Se creará la sanción de bloqueo igualmente." }
        }

        val nuevaSancionBloqueo = Sancion(
            user = user,
            prestamo = prestamo,
            tipoSancion = TipoSancion.BLOQUEO_TEMPORAL,
            fechaFin = LocalDate.now().plusMonths(2)
        )

        val sancionGuardada = repository.save(nuevaSancionBloqueo)

        enviarCorreo(user, "BLOQUEO TEMPORAL")
        sendNotificationNuevaSancion(sancionGuardada, "automática: $motivo")
    }

    /**
     * Crea una sanción de [TipoSancion.INDEFINIDO] para un usuario.
     * También inactiva al usuario si este está activo.
     *
     * @param user El usuario a sancionar.
     * @param motivo El motivo de la sanción.
     */
    @Transactional
    internal fun crearBloqueoIndefinido(user: User, motivo: String) {
        logger.info { "Creando sanción INDEFINIDA automática para usuario ${user.username}. Motivo: $motivo" }
        if (user.isActivo) {
            user.isActivo = false
            user.updatedDate = LocalDateTime.now()
            userRepository.save(user)
        } else {
            logger.warn { "Usuario ${user.username} ya está inactivo. Se creará la sanción indefinida igualmente." }
        }

        val nuevaSancionIndefinida = Sancion(
            user = user,
            tipoSancion = TipoSancion.INDEFINIDO
        )

        val sancionGuardada = repository.save(nuevaSancionIndefinida)

        enviarCorreo(user, "BLOQUEO INDEFINIDO")
        sendNotificationNuevaSancion(sancionGuardada, "automática: $motivo")
    }

    /**
     * Envía una notificación WebSocket a un usuario y a los administradores sobre una nueva sanción.
     *
     * @param sancion La sanción que se acaba de crear.
     * @param origenDetallado Una descripción detallada del origen o motivo de la sanción.
     */
    private fun sendNotificationNuevaSancion(sancion: Sancion, origenDetallado: String) {
        val user = sancion.user
        var tituloUser: String
        var mensajeUser: String
        var severidadUser: NotificationSeverityDto

        when (sancion.tipoSancion) {
            TipoSancion.ADVERTENCIA -> {
                tituloUser = "Advertencia Recibida"
                mensajeUser = "Has recibido una advertencia. Razón: $origenDetallado. Revisa tus préstamos y cumple las normativas."
                severidadUser = NotificationSeverityDto.WARNING
            }
            TipoSancion.BLOQUEO_TEMPORAL -> {
                tituloUser = "Cuenta Bloqueada Temporalmente"
                mensajeUser = "Tu cuenta ha sido bloqueada por 2 meses (hasta ${sancion.fechaFin}) debido a: $origenDetallado. No podrás realizar préstamos durante este periodo."
                severidadUser = NotificationSeverityDto.ERROR
            }
            TipoSancion.INDEFINIDO -> {
                tituloUser = "Cuenta Suspendida Indefinidamente"
                mensajeUser = "Tu cuenta ha sido suspendida indefinidamente debido a: $origenDetallado. Contacta a un administrador."
                severidadUser = NotificationSeverityDto.ERROR
            }
        }
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = tituloUser,
            mensaje = mensajeUser,
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SANCION,
            enlace = null,
            severidadSugerida = severidadUser,
            mostrarToast = true
        )

        webService.createAndSendNotification(user.email, notificacionParaUser)

        val administradores = userRepository.findUsersByRol(Role.ADMIN)
        administradores.forEach { admin ->
            if (admin != null && admin.email != user.email) {
                val notificacionParaAdmin = NotificationDto(
                    id = UUID.randomUUID().toString(),
                    titulo = "Nueva Sanción Aplicada: ${sancion.tipoSancion}",
                    mensaje = "Usuario ${user.nombre} ${user.apellidos} (Email: ${user.email}) ha recibido sanción ${sancion.tipoSancion} (GUID: ${sancion.guid}). Origen: $origenDetallado.",
                    fecha = LocalDateTime.now(),
                    leida = false,
                    tipo = NotificationTypeDto.SANCION,
                    enlace = "/admin/sancion/detalle/${sancion.guid}",
                    severidadSugerida = if (sancion.tipoSancion == TipoSancion.ADVERTENCIA) NotificationSeverityDto.INFO else NotificationSeverityDto.WARNING,
                    mostrarToast = false
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }

    /**
     * Envía una notificación WebSocket a un usuario y a los administradores sobre la reactivación de una cuenta.
     *
     * @param user El usuario que ha sido reactivado.
     * @param sancionQueFinaliza La sanción que finalizó, causando la reactivación (puede ser nula).
     * @param motivoReactivacion Una descripción del motivo de la reactivación.
     */
    private fun sendNotificationReactivacionUsuario(user: User, sancionQueFinaliza: Sancion?, motivoReactivacion: String) {
        val mensaje = if (sancionQueFinaliza != null) {
            "Tu cuenta ha sido reactivada. El periodo de bloqueo (${sancionQueFinaliza.guid}) ha finalizado. Motivo: $motivoReactivacion."
        } else {
            "Tu cuenta ha sido reactivada. Motivo: $motivoReactivacion."
        }
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Cuenta Reactivada",
            mensaje = mensaje,
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.INFO,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.SUCCESS,
            mostrarToast = false
        )
        webService.createAndSendNotification(user.email, notificacionParaUser)

        val administradores = userRepository.findUsersByRol(Role.ADMIN)
        administradores.forEach { admin ->
            if (admin != null) {
                val adminMsg = if (sancionQueFinaliza != null) {
                    "Usuario ${user.nombre} ${user.apellidos} reactivado tras finalizar sanción ${sancionQueFinaliza.guid}. Motivo: $motivoReactivacion."
                } else {
                    "Usuario ${user.nombre} ${user.apellidos} reactivado. Motivo: $motivoReactivacion."
                }
                val notificacionParaAdmin = NotificationDto(
                    id = UUID.randomUUID().toString(),
                    titulo = "Usuario Reactivado",
                    mensaje = adminMsg,
                    fecha = LocalDateTime.now(),
                    leida = false,
                    tipo = NotificationTypeDto.SISTEMA,
                    enlace = "/admin/usuario/detalle/${user.guid}",
                    severidadSugerida = NotificationSeverityDto.INFO,
                    mostrarToast = false
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }

    /**
     * Envía una notificación WebSocket a un usuario y a los administradores sobre una sanción eliminada.
     *
     * @param sancion La sanción que ha sido eliminada.
     */
    private fun sendNotificationSancionEliminada(sancion: Sancion) {
        val user = sancion.user
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Sanción Eliminada",
            mensaje = "Una sanción (Tipo: ${sancion.tipoSancion}, Fecha: ${sancion.fechaSancion}) que tenías registrada ha sido eliminada por un administrador.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SANCION,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.SUCCESS,
            mostrarToast = false
        )
        webService.createAndSendNotification(user.email, notificacionParaUser)

        val administradores = userRepository.findUsersByRol(Role.ADMIN)
        administradores.forEach { admin ->
            if (admin != null && admin.email != user.email) {
                val notificacionParaAdmin = NotificationDto(
                    id = UUID.randomUUID().toString(),
                    titulo = "Sanción Eliminada por Admin",
                    mensaje = "La sanción GUID ${sancion.guid} (Tipo: ${sancion.tipoSancion}) del usuario ${user.nombre} ${user.apellidos} ha sido eliminada.",
                    fecha = LocalDateTime.now(),
                    leida = false,
                    tipo = NotificationTypeDto.SANCION,
                    enlace = "/admin/dashboard/sanciones",
                    severidadSugerida = NotificationSeverityDto.INFO,
                    mostrarToast = false
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }

    /**
     * Envía una notificación WebSocket a un usuario y a los administradores sobre una sanción modificada.
     *
     * @param sancion La sanción que ha sido modificada.
     */
    private fun sendNotificationSancionModificada(sancion: Sancion) {
        val user = sancion.user
        val notificacionParaUser = NotificationDto(
            id = UUID.randomUUID().toString(),
            titulo = "Tu Sanción Ha Sido Modificada",
            mensaje = "Un administrador ha modificado tu sanción ${sancion.guid} (Tipo: ${sancion.tipoSancion}). Revisa los detalles en tu panel.",
            fecha = LocalDateTime.now(),
            leida = false,
            tipo = NotificationTypeDto.SANCION,
            enlace = null,
            severidadSugerida = NotificationSeverityDto.INFO,
            mostrarToast = true
        )
        webService.createAndSendNotification(user.email, notificacionParaUser)

        val administradores = userRepository.findUsersByRol(Role.ADMIN)
        administradores.forEach { admin ->
            if (admin != null && admin.email != user.email) {
                val notificacionParaAdmin = NotificationDto(
                    id = UUID.randomUUID().toString(),
                    titulo = "Sanción Modificada por Admin",
                    mensaje = "La sanción ${sancion.guid} del usuario ${user.username} ha sido modificada. Nuevo tipo: ${sancion.tipoSancion}, Fecha Fin: ${sancion.fechaFin}.",
                    fecha = LocalDateTime.now(),
                    leida = false,
                    tipo = NotificationTypeDto.SANCION,
                    enlace = "/admin/sancion/detalle/${sancion.guid}",
                    severidadSugerida = NotificationSeverityDto.INFO,
                    mostrarToast = false
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }

    /**
     * Envía un correo electrónico al usuario para notificarle de una sanción.
     *
     * @param user El usuario sancionado.
     * @param tipoSancion El tipo de sanción aplicada (String para el contenido del correo).
     */
    private fun enviarCorreo(user: User, tipoSancion: String) {
        emailService.sendHtmlEmailSancion(
            to = user.email,
            subject = "Notificación de Sanción LoanTech",
            nombreUsuario = user.nombre,
            tipoSancion = tipoSancion,
        )
    }

    /**
     * Envía un correo electrónico al usuario para notificarle la reactivación de su cuenta.
     *
     * @param user El usuario reactivado.
     */
    private fun enviarCorreoReactivacion(user: User) {
        emailService.sendHtmlEmailUsuarioReactivado(
            to = user.email,
            subject = "¡Tu cuenta en LoanTech ha sido reactivada!",
            nombreUsuario = user.nombre
        )
    }
}