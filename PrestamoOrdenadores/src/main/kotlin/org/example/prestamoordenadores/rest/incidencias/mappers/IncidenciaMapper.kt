package org.example.prestamoordenadores.rest.incidencias.mappers

import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponseAdmin
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class IncidenciaMapper {
    fun toIncidenciaResponse(incidencia: Incidencia) : IncidenciaResponse{
        val userMapper = UserMapper()

        return IncidenciaResponse(
            guid = incidencia.guid,
            asunto = incidencia.asunto,
            descripcion = incidencia.descripcion,
            estadoIncidencia = incidencia.estadoIncidencia.toString(),
            user = userMapper.toUserResponse(incidencia.user),
            createdDate = incidencia.createdDate.toDefaultDateTimeString()
        )
    }

    fun toIncidenciaResponseAdmin(incidencia: Incidencia) : IncidenciaResponseAdmin{
        val userMapper = UserMapper()

        return IncidenciaResponseAdmin(
            guid = incidencia.guid,
            asunto = incidencia.asunto,
            descripcion = incidencia.descripcion,
            estadoIncidencia = incidencia.estadoIncidencia.toString(),
            user = userMapper.toUserResponse(incidencia.user),
            createdDate = incidencia.createdDate.toDefaultDateTimeString(),
            updatedDate = incidencia.updatedDate.toDefaultDateTimeString(),
            isDeleted = incidencia.isDeleted
        )
    }

    fun toIncidenciaFromCreate(incidencia: IncidenciaCreateRequest, user: User) : Incidencia{
        return Incidencia(
            asunto = incidencia.asunto,
            descripcion = incidencia.descripcion,
            estadoIncidencia = EstadoIncidencia.PENDIENTE,
            user = user,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toIncidenciaResponseList(incidencias: List<Incidencia>): List<IncidenciaResponse> {
        return incidencias.map { toIncidenciaResponse(it) }
    }
}