package org.example.prestamoordenadores.rest.dispositivos.mappers

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.Estado
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DispositivoMapper {
    fun toDispositivoResponse(dispositivo: Dispositivo): DispositivoResponse{
        return DispositivoResponse(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            stock = dispositivo.stock
        )
    }

    fun toDispositivoFromCreate(dispositivo: DispositivoCreateRequest): Dispositivo {
        return Dispositivo(
            componentes = dispositivo.componentes,
            stock = dispositivo.stock,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toDispositivoFromUpdate(dispositivo: Dispositivo, request: DispositivoUpdateRequest): Dispositivo {
        return Dispositivo(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = request.componentes ?: dispositivo.componentes,
            estado = request.estado?.let { Estado.valueOf(it) } ?: dispositivo.estado,
            incidenciaGuid = request.incidenciaGuid ?: dispositivo.incidenciaGuid,
            stock = request.stock ?: dispositivo.stock,
            updatedDate = LocalDateTime.now()
        )
    }

    fun toDispositivoResponseAdmin(dispositivo: Dispositivo): DispositivoResponseAdmin{
        return DispositivoResponseAdmin(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            estado = dispositivo.estado.toString(),
            incidenciaGuid = dispositivo.incidenciaGuid,
            stock = dispositivo.stock,
            isActivo = dispositivo.isActivo
        )
    }

    fun toDispositivoResponseList(dispositivos: List<Dispositivo?>): List<DispositivoResponse> {
        return dispositivos.map { toDispositivoResponse(it!!) }
    }

    fun toDispositivoResponseListAdmin(dispositivos: List<Dispositivo?>): List<DispositivoResponseAdmin> {
        return dispositivos.map { toDispositivoResponseAdmin(it!!) }
    }
}