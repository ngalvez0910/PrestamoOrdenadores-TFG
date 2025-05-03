package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service

@Service
interface DispositivoService {
    fun getAllDispositivos(page: Int, size: Int): Result<PagedResponse<DispositivoResponseAdmin>, DispositivoError>
    fun getDispositivoByGuid(guid: String) : Result<DispositivoResponseAdmin?, DispositivoError>
    fun createDispositivo(dispositivo: DispositivoCreateRequest) : Result<DispositivoResponse, DispositivoError>
    fun updateDispositivo(guid: String, dispositivo: DispositivoUpdateRequest) : Result<DispositivoResponseAdmin?, DispositivoError>
    fun deleteDispositivoByGuid(guid: String) : Result<DispositivoResponse?, DispositivoError>
    fun getDispositivoByNumeroSerie(numeroSerie: String) : Result<DispositivoResponseAdmin?, DispositivoError>
    fun getDispositivoByEstado(estado: String): Result<List<DispositivoResponseAdmin>, DispositivoError>
    fun getStock(): Result<Int, DispositivoError>
}