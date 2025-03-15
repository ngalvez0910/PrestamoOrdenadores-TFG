package org.example.prestamoordenadores.rest.incidencias.mappers

import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.springframework.stereotype.Component

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

    fun toIncidenciaResponseList(incidencias: List<Incidencia?>): List<IncidenciaResponse> {
        return incidencias.map { toIncidenciaResponse(it!!) }
    }
}