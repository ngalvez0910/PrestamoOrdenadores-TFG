package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.rest.dispositivos.mappers.DispositivoMapper
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.utils.pagination.PagedResponse
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
    private val mapper: DispositivoMapper,
    private val incidenciaRepository: IncidenciaRepository
) : DispositivoService {
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

    @CachePut(key = "#result.guid")
    override fun createDispositivo(dispositivo: DispositivoCreateRequest): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Guardando un nuevo dispositivo" }

        val dispositivoValidado = dispositivo.validate()
        if (dispositivoValidado.isErr) {
            return Err(DispositivoError.DispositivoValidationError("Dispositivo inválido"))
        }

        val newDispositivo = mapper.toDispositivoFromCreate(dispositivo)
        dispositivoRepository.save(newDispositivo)

        return Ok(mapper.toDispositivoResponse(newDispositivo))
    }

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
        dispositivo.isActivo?.let { existingDispositivo.isActivo = it }

        dispositivoRepository.save(existingDispositivo)

        return Ok(mapper.toDispositivoResponseAdmin(existingDispositivo))
    }

    @CachePut(key = "#guid")
    override fun deleteDispositivoByGuid(guid: String): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Eliminando dispositivo con GUID: $guid" }

        val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        if (dispositivo == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        }

        dispositivo.estadoDispositivo = EstadoDispositivo.NO_DISPONIBLE
        dispositivo.isActivo = false
        dispositivo.updatedDate = LocalDateTime.now()

        dispositivoRepository.save(dispositivo)

        return Ok(mapper.toDispositivoResponse(dispositivo))
    }

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

    override fun getStock(): Result<Int, DispositivoError> {
        logger.debug { "Obteniendo stock" }

        return Ok(dispositivoRepository.findAll().count())
    }
}