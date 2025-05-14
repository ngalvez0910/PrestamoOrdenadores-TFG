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
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
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

@Service
@CacheConfig(cacheNames = ["sanciones"])
class SancionServiceImpl(
    private val repository : SancionRepository,
    private val mapper: SancionMapper,
    private val userRepository: UserRepository,
    private val prestamoRepository: PrestamoRepository,
    private val webService : WebSocketService,
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

    @CachePut(key = "#guid")
    override fun deleteSancionByGuid(guid: String): Result<SancionResponse?, SancionError> {
        logger.debug { "Buscando sancion" }

        val existingSancion = repository.findByGuid(guid)
        if (existingSancion == null) {
            return Err(SancionError.SancionNotFound("Sancion no encontrada"))
        }

        logger.debug { "Eliminando sancion" }
        repository.delete(existingSancion)

        sendNotificationSancionEliminada(existingSancion)
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

    //@Scheduled(cron = "0 0 0/2 * * *")
    @Scheduled(fixedRate = 60000)
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

    //@Scheduled(cron = "0 5 0/2 * * *")
    @Scheduled(fixedRate = 60000)
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
                    sendNotificationReactivacionUsuario(user, sancionBloqueo, "finalización de bloqueo temporal")
                } else {
                    logger.info { "Usuario ${user.username} no reactivado (sanción ${sancionBloqueo.guid} expirada) debido a otras sanciones bloqueantes activas." }
                }
            }

            evaluarPasoAIndefinido(user)
        }
        logger.info { "Tarea programada: Gestionar Reactivación y Escalada a Indefinido finalizada." }
    }

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

        sendNotificationNuevaSancion(sancionGuardada, "automática: $motivo")
    }

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

        sendNotificationNuevaSancion(sancionGuardada, "automática: $motivo")
    }

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
            severidadSugerida = severidadUser
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
                    severidadSugerida = if (sancion.tipoSancion == TipoSancion.ADVERTENCIA) NotificationSeverityDto.INFO else NotificationSeverityDto.WARNING
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }

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
            severidadSugerida = NotificationSeverityDto.SUCCESS
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
                    severidadSugerida = NotificationSeverityDto.INFO
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }

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
            severidadSugerida = NotificationSeverityDto.SUCCESS
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
                    severidadSugerida = NotificationSeverityDto.INFO
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }

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
            severidadSugerida = NotificationSeverityDto.INFO
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
                    severidadSugerida = NotificationSeverityDto.INFO
                )
                webService.createAndSendNotification(admin.email, notificacionParaAdmin)
            }
        }
    }
}