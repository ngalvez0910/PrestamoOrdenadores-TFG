package org.example.prestamoordenadores.rest.dispositivos.mappers

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
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

    fun toDispositivoResponseAdmin(dispositivo: Dispositivo): DispositivoResponseAdmin{
        return DispositivoResponseAdmin(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            estado = dispositivo.estadoDispositivo.toString(),
            incidenciaGuid = dispositivo.incidenciaGuid ?:"",
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