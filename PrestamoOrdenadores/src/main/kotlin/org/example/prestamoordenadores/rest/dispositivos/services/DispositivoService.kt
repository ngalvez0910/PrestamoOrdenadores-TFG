package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.springframework.stereotype.Service

@Service
interface DispositivoService {
    suspend fun getAllDispositivos(page: Int, size: Int): Result<List<DispositivoResponseAdmin>, DispositivoError>
    suspend fun getDispositivoByGuid(guid: String) : Result<DispositivoResponseAdmin?, DispositivoError>
    suspend fun createDispositivo(dispositivo: DispositivoCreateRequest) : Result<DispositivoResponse, DispositivoError>
    suspend fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest) : Result<DispositivoResponseAdmin?, DispositivoError>
    suspend fun deleteDispositivoByGuid(guid: String) : Result<DispositivoResponse?, DispositivoError>
    suspend fun getDispositivoByNumeroSerie(numeroSerie: String) : Result<DispositivoResponseAdmin?, DispositivoError>
    suspend fun getDispositivoByEstado(estado: String): Result<List<DispositivoResponseAdmin>, DispositivoError>
}