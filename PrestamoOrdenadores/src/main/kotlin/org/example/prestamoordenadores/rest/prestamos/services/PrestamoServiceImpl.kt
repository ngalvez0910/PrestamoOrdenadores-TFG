package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.rest.prestamos.mappers.PrestamoMapper
import org.example.prestamoordenadores.rest.prestamos.models.Estado
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

private val logger = logging()

@Service
class PrestamoServiceImpl(
    private val prestamoRepository: PrestamoRepository,
    private val mapper: PrestamoMapper,
    private val userRepository: UserRepository,
    private val dispositivoRepository: DispositivoRepository
): PrestamoService {
    override fun getAllPrestamos(): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo todos los prestamos" }
        var prestamos = prestamoRepository.findAll()
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    override fun getPrestamoByGuid(guid: String): Result<PrestamoResponse?, PrestamoError> {
        logger.debug { "Obteniendo prestamo con GUID: $guid" }
        val prestamo = prestamoRepository.findByGuid(guid)

        return if (prestamo == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            Ok(mapper.toPrestamoResponse(prestamo))
        }
    }

    override fun createPrestamo(prestamo: PrestamoCreateRequest): Result<PrestamoResponse, PrestamoError> {
        logger.debug { "Creando nuevo prestamo" }
        val user = userRepository.findByGuid(prestamo.userGuid)
        if (user == null) {
            return Err(PrestamoError.UserNotFound("Usuario con GUID: ${prestamo.userGuid} no encontrado"))
        }

        val dispositivo = dispositivoRepository.findDispositivoByGuid(prestamo.dispositivoGuid)
        if (dispositivo == null) {
            return Err(PrestamoError.DispositivoNotFound("Dispositivo con GUID: ${prestamo.dispositivoGuid} no encontrado"))
        }

        var prestamoCreado = mapper.toPrestamoFromCreate(prestamo)

        return Ok(mapper.toPrestamoResponse(prestamoCreado))
    }

    override fun updatePrestamo(guid: String, prestamo: PrestamoUpdateRequest): Result<PrestamoResponse?, PrestamoError> {
        logger.debug { "Actualizando prestamo con GUID: $guid" }
        val prestamoEncontrado = prestamoRepository.findByGuid(guid)

        return if (prestamoEncontrado == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            prestamoEncontrado.estado = prestamo.estado
            prestamoEncontrado.updatedDate = LocalDateTime.now()

            prestamoRepository.save(prestamoEncontrado)
            Ok(mapper.toPrestamoResponse(prestamoEncontrado))
        }
    }

    override fun deletePrestamoByGuid(guid: String): Result<PrestamoResponse, PrestamoError> {
        logger.debug { "Cancelando prestamo con GUID: $guid" }
        val prestamoEncontrado = prestamoRepository.findByGuid(guid)

        return if (prestamoEncontrado == null) {
            Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
        } else {
            prestamoEncontrado.estado = Estado.CANCELADO

            prestamoRepository.save(prestamoEncontrado)
            Ok(mapper.toPrestamoResponse(prestamoEncontrado))
        }
    }

    override fun getByFechaPrestamo(fecha: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de prestamo: $fecha" }

        val prestamos = prestamoRepository.findByFechaPrestamo(fecha)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    override fun getByFechaDevolucion(fecha: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de devolucion: $fecha" }

        val prestamos = prestamoRepository.findByFechaDevolucion(fecha)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }

    override fun getPrestamoByUserGuid(userGuid: String): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos del usuario con GUID: $userGuid" }

        val user = userRepository.findByGuid(userGuid)
        if (user == null) {
            return Err(PrestamoError.UserNotFound("Usuario con GUID: $userGuid no encontrado"))
        }

        val prestamos = prestamoRepository.findByUserGuid(userGuid)
        return Ok(mapper.toPrestamoResponseList(prestamos))
    }
}