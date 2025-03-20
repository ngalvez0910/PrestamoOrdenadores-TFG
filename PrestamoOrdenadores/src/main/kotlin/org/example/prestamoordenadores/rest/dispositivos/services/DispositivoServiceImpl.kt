package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.rest.dispositivos.mappers.DispositivoMapper
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["dispositivos"])
class DispositivoServiceImpl(
    private val dispositivoRepository: DispositivoRepository,
    private val mapper: DispositivoMapper
) : DispositivoService {
    override suspend fun getAllDispositivos(page: Int, size: Int): Result<List<DispositivoResponseAdmin>, DispositivoError> {
        logger.debug { "Obteniendo todos los dispositivos" }

        return withContext(Dispatchers.IO) {
            val pageRequest = PageRequest.of(page, size)
            val pageDispositivos = dispositivoRepository.findAll(pageRequest)
            val dispositivoResponses = mapper.toDispositivoResponseListAdmin(pageDispositivos.content)

            Ok(dispositivoResponses)
        }
    }

    @Cacheable(key = "#guid")
    override suspend fun getDispositivoByGuid(guid: String): Result<DispositivoResponseAdmin?, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)

            if (dispositivo == null) {
                Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
            } else {
                Ok(mapper.toDispositivoResponseAdmin(dispositivo))
            }
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun createDispositivo(dispositivo: DispositivoCreateRequest): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Guardando un nuevo dispositivo" }

        return withContext(Dispatchers.IO) {
            val dispositivoValidado = dispositivo.validate()
            if (dispositivoValidado.isErr) {
                return@withContext Err(DispositivoError.DispositivoValidationError("Dispositivo inválido"))
            }

            val newDispositivo = mapper.toDispositivoFromCreate(dispositivo)
            dispositivoRepository.save(newDispositivo)

            Ok(mapper.toDispositivoResponse(newDispositivo))
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest): Result<DispositivoResponseAdmin, DispositivoError> {
        logger.debug { "Actualizando dispositivo con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val existingDispositivo = dispositivoRepository.findDispositivoByGuid(guid)
            if (existingDispositivo == null) {
                return@withContext Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
            }

            dispositivo.componentes?.let { existingDispositivo.componentes = it }
            dispositivo.estado?.let { existingDispositivo.estadoDispositivo = EstadoDispositivo.valueOf(it) }
            dispositivo.incidenciaGuid?.let { existingDispositivo.incidenciaGuid = it }
            dispositivo.stock?.let { existingDispositivo.stock = it }
            dispositivo.isActivo?.let { existingDispositivo.isActivo = it }

            dispositivoRepository.save(existingDispositivo)

            Ok(mapper.toDispositivoResponseAdmin(existingDispositivo))
        }
    }

    @CachePut(key = "#guid")
    override suspend fun deleteDispositivoByGuid(guid: String): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Eliminando dispositivo con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)
            if (dispositivo == null) {
                return@withContext Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
            }

            dispositivo.estadoDispositivo = EstadoDispositivo.NO_DISPONIBLE
            dispositivo.isActivo = false
            dispositivo.updatedDate = LocalDateTime.now()

            dispositivoRepository.save(dispositivo)

            Ok(mapper.toDispositivoResponse(dispositivo))
        }
    }

    @Cacheable(key = "#numeroSerie")
    override suspend fun getDispositivoByNumeroSerie(numeroSerie: String): Result<DispositivoResponseAdmin, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con numero de serie: $numeroSerie" }

        return withContext(Dispatchers.IO) {
            val dispositivo = dispositivoRepository.findByNumeroSerie(numeroSerie)

            if (dispositivo == null) {
                Err(DispositivoError.DispositivoNotFound("Dispositivo con numero de serie: $numeroSerie no encontrado"))
            } else {
                Ok(mapper.toDispositivoResponseAdmin(dispositivo))
            }
        }
    }

    @Cacheable(key = "#estado")
    override suspend fun getDispositivoByEstado(estado: String): Result<List<DispositivoResponseAdmin>, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con estado: $estado" }

        return withContext(Dispatchers.IO) {
            val estadoNormalizado = estado.replace(" ", "_").uppercase()
            val estadoEnum = EstadoDispositivo.entries.find { it.name == estadoNormalizado }

            if (estadoEnum == null) {
                return@withContext Err(DispositivoError.DispositivoNotFound("Dispositivo con estado '$estado' no encontrado"))
            }

            val dispositivos = dispositivoRepository.findByEstadoDispositivo(estadoEnum)

            Ok(mapper.toDispositivoResponseListAdmin(dispositivos))
        }
    }
}