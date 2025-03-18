package org.example.prestamoordenadores.rest.prestamos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.rest.prestamos.mappers.PrestamoMapper
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
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
    private val dispositivoRepository: DispositivoRepository
): PrestamoService {
    override suspend fun getAllPrestamos(page: Int, size: Int): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo todos los prestamos" }

        return withContext(Dispatchers.IO) {
            val pageRequest = PageRequest.of(page, size)
            val pagePrestamos = prestamoRepository.findAll(pageRequest)
            val prestamoResponses = mapper.toPrestamoResponseList(pagePrestamos.content)

            Ok(prestamoResponses)
        }
    }

    @Cacheable(key = "#guid")
    override suspend fun getPrestamoByGuid(guid: String): Result<PrestamoResponse?, PrestamoError> {
        logger.debug { "Obteniendo prestamo con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val prestamo = prestamoRepository.findByGuid(guid)

            if (prestamo == null) {
                Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
            } else {
                Ok(mapper.toPrestamoResponse(prestamo))
            }
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun createPrestamo(prestamo: PrestamoCreateRequest): Result<PrestamoResponse, PrestamoError> {
        logger.debug { "Creando nuevo prestamo" }

        return withContext(Dispatchers.IO) {
            val user = userRepository.findByGuid(prestamo.userGuid)
            if (user == null) {
                return@withContext Err(PrestamoError.UserNotFound("Usuario con GUID: ${prestamo.userGuid} no encontrado"))
            }

            val dispositivosDisponibles = dispositivoRepository.findByEstadoDispositivo(EstadoDispositivo.NUEVO)
            if (dispositivosDisponibles.isEmpty()) {
                return@withContext Err(PrestamoError.DispositivoNotFound("No hay dispositivos disponibles actualmente"))
            }

            val dispositivoSeleccionado = dispositivosDisponibles.random()

            var prestamoCreado = mapper.toPrestamoFromCreate(prestamo, dispositivoSeleccionado)
            prestamoCreado.fechaDevolucion = LocalDate.now().plusWeeks(3)
            prestamoRepository.save(prestamoCreado)

            dispositivoSeleccionado.estadoDispositivo = EstadoDispositivo.PRESTADO
            dispositivoRepository.save(dispositivoSeleccionado)

            Ok(mapper.toPrestamoResponse(prestamoCreado))
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun updatePrestamo(guid: String, prestamo: PrestamoUpdateRequest): Result<PrestamoResponse?, PrestamoError> {
        logger.debug { "Actualizando prestamo con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val prestamoEncontrado = prestamoRepository.findByGuid(guid)

            if (prestamoEncontrado == null) {
                Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
            } else {
                prestamoEncontrado.estadoPrestamo = EstadoPrestamo.valueOf(prestamo.estado.uppercase())
                prestamoEncontrado.updatedDate = LocalDateTime.now()

                prestamoRepository.save(prestamoEncontrado)
                Ok(mapper.toPrestamoResponse(prestamoEncontrado))
            }
        }
    }

    @CachePut(key = "#guid")
    override suspend fun deletePrestamoByGuid(guid: String): Result<PrestamoResponse, PrestamoError> {
        logger.debug { "Cancelando prestamo con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val prestamoEncontrado = prestamoRepository.findByGuid(guid)

            if (prestamoEncontrado == null) {
                Err(PrestamoError.PrestamoNotFound("Prestamo con GUID: $guid no encontrado"))
            } else {
                prestamoEncontrado.estadoPrestamo = EstadoPrestamo.CANCELADO
                prestamoRepository.save(prestamoEncontrado)
                Ok(mapper.toPrestamoResponse(prestamoEncontrado))
            }
        }
    }

    @Cacheable(key = "#fechaPrestamo")
    override suspend fun getByFechaPrestamo(fechaPrestamo: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de prestamo: $fechaPrestamo" }

        return withContext(Dispatchers.IO) {
            val prestamos = prestamoRepository.findByFechaPrestamo(fechaPrestamo)
            Ok(mapper.toPrestamoResponseList(prestamos))
        }
    }

    @Cacheable(key = "#fechaDevolucion")
    override suspend fun getByFechaDevolucion(fechaDevolucion: LocalDate): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos con fecha de devolucion: $fechaDevolucion" }

        return withContext(Dispatchers.IO) {
            val prestamos = prestamoRepository.findByFechaDevolucion(fechaDevolucion)
            Ok(mapper.toPrestamoResponseList(prestamos))
        }
    }

    @Cacheable(key = "#userGuid")
    override suspend fun getPrestamoByUserGuid(userGuid: String): Result<List<PrestamoResponse>, PrestamoError> {
        logger.debug { "Obteniendo prestamos del usuario con GUID: $userGuid" }

        return withContext(Dispatchers.IO) {
            val user = userRepository.findByGuid(userGuid)
            if (user == null) {
                return@withContext Err(PrestamoError.UserNotFound("Usuario con GUID: $userGuid no encontrado"))
            }

            val prestamos = prestamoRepository.findByUserGuid(userGuid)
            Ok(mapper.toPrestamoResponseList(prestamos))
        }
    }
}