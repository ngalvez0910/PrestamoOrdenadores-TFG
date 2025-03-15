package org.example.prestamoordenadores.rest.incidencias.mappers

import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class IncidenciaMapper {
    fun toIncidenciaResponse(incidencia: Incidencia) : IncidenciaResponse{
        return IncidenciaResponse(
            guid = incidencia.guid,
            asunto = incidencia.asunto,
            descripcion = incidencia.descripcion,
            estadoIncidencia = incidencia.estadoIncidencia.toString(),
            userGuid = incidencia.userGuid,
            createdDate = incidencia.createdDate.toString()
        )
    }

    fun toIncidenciaFromCreate(incidencia: IncidenciaCreateRequest) : Incidencia{
        return Incidencia(
            asunto = incidencia.asunto,
            descripcion = incidencia.descripcion,
            estadoIncidencia = EstadoIncidencia.PENDIENTE,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toIncidenciaResponseList(incidencias: List<Incidencia?>): List<IncidenciaResponse> {
        return incidencias.map { toIncidenciaResponse(it!!) }
    }
}