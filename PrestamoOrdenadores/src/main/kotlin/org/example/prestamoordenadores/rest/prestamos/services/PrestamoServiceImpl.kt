package org.example.prestamoordenadores.rest.prestamos.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.config.websockets.WebSocketConfig
import org.example.prestamoordenadores.config.websockets.WebSocketHandler
import org.example.prestamoordenadores.config.websockets.models.Notification
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
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["prestamos"])
class PrestamoServiceImpl(
    private val prestamoRepository: PrestamoRepository,
    private val mapper: PrestamoMapper,
    private val userRepository: UserRepository,
    private val dispositivoRepository: DispositivoRepository,
    private val prestamoPdfStorage: PrestamoPdfStorage,
    private val webSocketConfig: WebSocketConfig,
    private val objectMapper: ObjectMapper,
    @Qualifier("webSocketPrestamosHandler") private val webSocketHandler: WebSocketHandler,
    private val emailService: EmailService
): PrestamoService {
    override fun getAllPrestamos(page: Int, size: Int): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo todos los prestamos" }

        val pageRequest = PageRequest.of(page, size)
        val pagePrestamos = prestamoRepository.findAll(pageRequest)
        val prestamoResponses = mapper.toPrestamoResponseList(pagePrestamos.content)

        return Ok(prestamoResponses)
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

        onChange(Notification.Tipo.CREATE, prestamoCreado)
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
            prestamoEncontrado.estadoPrestamo = EstadoPrestamo.valueOf(prestamo.estado.uppercase())
            prestamoEncontrado.updatedDate = LocalDateTime.now()

            prestamoRepository.save(prestamoEncontrado)

            onChange(Notification.Tipo.UPDATE, prestamoEncontrado)
            Ok(mapper.toPrestamoResponse(prestamoEncontrado))
        }
    }

    @CachePut(key = "#guid")
    override fun deletePrestamoByGuid(guid: String): Result<PrestamoResponse, PrestamoError> {
        logger.debug { "Cancelando prestamo con GUID: $guid" }

        val prestamoEncontrado = prestamoRepository.findByGuid(guid)

        return if (prestamoEncontrado == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            prestamoEncontrado.estadoPrestamo = EstadoPrestamo.CANCELADO
            prestamoRepository.save(prestamoEncontrado)

            onChange(Notification.Tipo.DELETE, prestamoEncontrado)
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

    fun onChange(tipo: Notification.Tipo?, prestamo: Prestamo) {
        logger.info { "Servicio de Prestamos onChange con tipo: $tipo y prestamo GUID: ${prestamo.guid}" }

        try {
            val prestamoResponse = mapper.toPrestamoResponse(prestamo)
            val notificacion = Notification(
                "PRESTAMOS",
                tipo,
                prestamoResponse,
                LocalDateTime.now().toString()
            )

            val json = objectMapper.writeValueAsString(notificacion)

            val creatorUsername = prestamo.user.username
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
}