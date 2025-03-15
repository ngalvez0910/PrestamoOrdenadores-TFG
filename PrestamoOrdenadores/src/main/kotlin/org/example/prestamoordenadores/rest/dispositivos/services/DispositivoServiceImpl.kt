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
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
class DispositivoServiceImpl(
    private val dispositivoRepository: DispositivoRepository,
    private val mapper: DispositivoMapper
) : DispositivoService {
    override fun getAllDispositivos(): Result<List<DispositivoResponseAdmin>, DispositivoError> {
        logger.debug { "Obteniendo todos los dispositivos" }
        var dispositivos = dispositivoRepository.findAll()

        return Ok(mapper.toDispositivoResponseListAdmin(dispositivos))
    }

    override fun getDispositivoByGuid(guid: String): Result<DispositivoResponseAdmin?, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con GUID: $guid" }
        val dispositivo = dispositivoRepository.findDispositivoByGuid(guid)

        return if (dispositivo == null) {
            Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        } else {
            Ok(mapper.toDispositivoResponseAdmin(dispositivo))
        }
    }

    override fun createDispositivo(dispositivo: DispositivoCreateRequest): Result<DispositivoResponse, DispositivoError> {
        logger.debug { "Guardando un nuevo dispositivo" }
        var newDispositivo = mapper.toDispositivoFromCreate(dispositivo)

        dispositivoRepository.save(newDispositivo)

        return Ok(mapper.toDispositivoResponse(newDispositivo))
    }

    override fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest): Result<DispositivoResponseAdmin, DispositivoError> {
        logger.debug { "Actualizando dispositivo con GUID: $guid" }
        val existingDispositivo = dispositivoRepository.findDispositivoByGuid(guid)
        if (existingDispositivo == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con GUID: $guid no encontrado"))
        }

        dispositivo.componentes?.let { existingDispositivo.componentes = it }
        dispositivo.estado?.let { existingDispositivo.estadoDispositivo = EstadoDispositivo.valueOf(it) }
        dispositivo.incidenciaGuid?.let { existingDispositivo.incidenciaGuid = it }
        dispositivo.stock?.let { existingDispositivo.stock = it }
        dispositivo.isActivo?.let { existingDispositivo.isActivo = it }

        dispositivoRepository.save(existingDispositivo)

        return Ok(mapper.toDispositivoResponseAdmin(existingDispositivo))
    }

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

    override fun getDispositivoByNumeroSerie(numeroSerie: String): Result<DispositivoResponseAdmin, DispositivoError> {
        logger.debug { "Obteniendo dispositivo con numero de serie: $numeroSerie" }
        val dispositivo = dispositivoRepository.findByNumeroSerie(numeroSerie)

        return if (dispositivo == null) {
            Err(DispositivoError.DispositivoNotFound("Dispositivo con numero de serie: $numeroSerie no encontrado"))
        } else {
            Ok(mapper.toDispositivoResponseAdmin(dispositivo))
        }
    }

    override fun getDispositivoByEstado(estado: String): Result<List<DispositivoResponseAdmin>, DispositivoError> {
        logger.debug{ "Obteniendo dispositivo con estado: $estado" }
        val estadoNormalizado = estado.replace(" ", "_").uppercase()

        val estadoEnum = EstadoDispositivo.entries.find { it.name == estadoNormalizado }
        if (estadoEnum == null) {
            return Err(DispositivoError.DispositivoNotFound("Dispositivo con estado '$estado' no encontrado"))
        }

        val dispositivos = dispositivoRepository.findByEstadoDispositivo(estadoEnum)

        return Ok(mapper.toDispositivoResponseListAdmin(dispositivos))
    }
}