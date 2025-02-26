package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.springframework.stereotype.Service

@Service
interface DispositivoService {
    fun getAllDispositivos(): Result<List<DispositivoResponse>, DispositivoError>
    fun getDispositivoByGuid(guid: String) : Result<DispositivoResponse?, DispositivoError>
    fun createDispositivo(dispositivo: DispositivoCreateRequest) : Result<DispositivoResponse, DispositivoError>
    fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest) : Result<DispositivoResponse?, DispositivoError>
    fun deleteDispositivoByGuid(guid: String) : Result<DispositivoResponse?, DispositivoError>
    fun getDispositivoByNumeroSerie(numeroSerie: String) : Result<DispositivoResponse?, DispositivoError>
    fun getDispositivoByEstado(estado: String): Result<DispositivoResponse?, DispositivoError>
}