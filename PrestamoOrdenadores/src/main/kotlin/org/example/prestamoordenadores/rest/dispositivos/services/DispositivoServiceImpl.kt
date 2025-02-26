package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.rest.dispositivos.mappers.DispositivoMapper
import org.example.prestamoordenadores.rest.dispositivos.models.Estado
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
class DispositivoServiceImpl(
    private val dispositivoRepository: DispositivoRepository,
    private val mapper: DispositivoMapper
) : DispositivoService {
    override fun getAllDispositivos(): Result<List<DispositivoResponse>, DispositivoError> {
        logger.debug { "Obteniendo todos los dispositivos" }
        var dispositivos = dispositivoRepository.findAll()
        return Ok(mapper.toDispositivoResponseList(dispositivos))
    }

    override fun getDispositivoByGuid(guid: String): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con GUID: $guid" }
        val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        return if (dispositivo == null) {
            Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        } else {
            Ok(mapper.toDispositivoResponse(dispositivo))
        }
    }

    override fun createDispositivo(dispositivo: DispositivoCreateRequest): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Guardando un nuevo dispositivo" }
        var newDispositivo = mapper.toDispositivoFromCreate(dispositivo)
        dispositivoRepository.save(newDispositivo)
        return Ok(mapper.toDispositivoResponse(newDispositivo))
    }

    override fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Actualizando dispositivo con GUID: $guid" }
        val existingDispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        if (existingDispositivo == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        }
        existingDispositivo.componentes = dispositivo.componentes
        existingDispositivo.estado = Estado.valueOf(dispositivo.estado)
        existingDispositivo.incidenciaGuid = dispositivo.incidenciaGuid
        existingDispositivo.stock = dispositivo.stock
        existingDispositivo.updatedDate = LocalDateTime.now()
        dispositivoRepository.save(existingDispositivo)
        return Ok(mapper.toDispositivoResponse(existingDispositivo))
    }

    override fun deleteDispositivoByGuid(guid: String): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Eliminando dispositivo con GUID: $guid" }
        val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        if (dispositivo == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        }
        dispositivo.estado = Estado.NO_DISPONIBLE
        dispositivo.isActivo = false
        dispositivo.updatedDate = LocalDateTime.now()
        dispositivoRepository.save(dispositivo)
        return Ok(mapper.toDispositivoResponse(dispositivo))
    }

    override fun getDispositivoByNumeroSerie(numeroSerie: String): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con numero de serie: $numeroSerie" }
        val dispositivo = dispositivoRepository.findByNumeroSerie(numeroSerie)
        return if (dispositivo == null) {
            Err(DispositivoError.DispositivoNotFound("Dispositivo con numero de serie: $numeroSerie no encontrado"))
        } else {
            Ok(mapper.toDispositivoResponse(dispositivo))
        }
    }

    override fun getDispositivoByEstado(estado: String): Result<List<DispositivoResponse>, DispositivoError> {
        logger.debug{ "Obteniendo dispositivo con estado: $estado" }
        val dispositivos = dispositivoRepository.findByEstado(estado)
        return Ok(mapper.toDispositivoResponseList(dispositivos))
    }
}