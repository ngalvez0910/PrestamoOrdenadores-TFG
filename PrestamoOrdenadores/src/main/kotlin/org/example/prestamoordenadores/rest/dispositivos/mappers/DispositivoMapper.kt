package org.example.prestamoordenadores.rest.dispositivos.mappers

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.springframework.stereotype.Component

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

    fun toDispositivoResponseList(dispositivos: List<Dispositivo?>): List<DispositivoResponse> {
        return dispositivos.map { toDispositivoResponse(it!!) }
    }
}