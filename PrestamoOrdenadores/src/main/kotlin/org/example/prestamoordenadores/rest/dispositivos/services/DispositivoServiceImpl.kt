package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.springframework.stereotype.Service

@Service
class DispositivoServiceImpl(
    private val dispositivoRepository: DispositivoRepository
) : DispositivoService {
    override fun getAllDispositivos(): Result<List<DispositivoResponse>, DispositivoError> {
        TODO("Not yet implemented")
    }

    override fun getDispositivoByGuid(guid: String): Result<DispositivoResponse, DispositivoError> {
        TODO("Not yet implemented")
    }

    override fun createDispositivo(dispositivo: DispositivoCreateRequest): Result<DispositivoResponse, DispositivoError> {
        TODO("Not yet implemented")
    }

    override fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest): Result<DispositivoResponse, DispositivoError> {
        TODO("Not yet implemented")
    }

    override fun deleteDispositivoByGuid(guid: String): Result<DispositivoResponse, DispositivoError> {
        TODO("Not yet implemented")
    }

    override fun getDispositivoByNumeroSerie(numeroSerie: String): Result<DispositivoResponse, DispositivoError> {
        TODO("Not yet implemented")
    }

    override fun getDispositivoByEstado(estado: String): Result<DispositivoResponse, DispositivoError> {
        TODO("Not yet implemented")
    }
}