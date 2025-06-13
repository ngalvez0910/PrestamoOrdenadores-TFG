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

/**
 * Mapper para la conversión entre entidades [Incidencia] y sus DTOs.
 *
 * Esta clase se encarga de transformar objetos de solicitud y entidades de base de datos
 * en objetos de respuesta adecuados para la API, y viceversa.
 *
 * @author Natalia González Álvarez
 */
@Component
class IncidenciaMapper {
    /**
     * Convierte una entidad [Incidencia] a un [IncidenciaResponse] básico.
     *
     * @param incidencia La entidad [Incidencia] a convertir.
     * @return Un [IncidenciaResponse] con los datos principales de la incidencia.
     * @author Natalia González Álvarez
     */
    fun toIncidenciaResponse(incidencia: Incidencia) : IncidenciaResponse{
        val userMapper = UserMapper() // Instancia un UserMapper para mapear el usuario asociado

        return IncidenciaResponse(
            guid = incidencia.guid,
            asunto = incidencia.asunto,
            descripcion = incidencia.descripcion,
            estadoIncidencia = incidencia.estadoIncidencia.toString(),
            user = userMapper.toUserResponse(incidencia.user), // Mapea el usuario a su DTO de respuesta
            createdDate = incidencia.createdDate.toDefaultDateTimeString() // Formatea la fecha de creación
        )
    }

    /**
     * Convierte una entidad [Incidencia] a un [IncidenciaResponseAdmin].
     *
     * Incluye información adicional como fechas de actualización y el estado de eliminación lógica,
     * adecuada para vistas administrativas.
     *
     * @param incidencia La entidad [Incidencia] a convertir.
     * @return Un [IncidenciaResponseAdmin] con todos los detalles de la incidencia.
     * @author Natalia González Álvarez
     */
    fun toIncidenciaResponseAdmin(incidencia: Incidencia) : IncidenciaResponseAdmin{
        val userMapper = UserMapper() // Instancia un UserMapper para mapear el usuario asociado

        return IncidenciaResponseAdmin(
            guid = incidencia.guid,
            asunto = incidencia.asunto,
            descripcion = incidencia.descripcion,
            estadoIncidencia = incidencia.estadoIncidencia.toString(),
            user = userMapper.toUserResponse(incidencia.user), // Mapea el usuario a su DTO de respuesta
            createdDate = incidencia.createdDate.toDefaultDateTimeString(), // Formatea la fecha de creación
            updatedDate = incidencia.updatedDate.toDefaultDateTimeString(), // Formatea la fecha de última actualización
            isDeleted = incidencia.isDeleted
        )
    }

    /**
     * Convierte un [IncidenciaCreateRequest] y un [User] a una entidad [Incidencia].
     *
     * Establece el estado inicial de la incidencia como [EstadoIncidencia.PENDIENTE]
     * y las fechas de creación y actualización al momento actual.
     *
     * @param incidencia La solicitud [IncidenciaCreateRequest] a convertir.
     * @param user El [User] asociado a esta incidencia.
     * @return Una entidad [Incidencia] lista para ser persistida.
     * @author Natalia González Álvarez
     */
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

    /**
     * Convierte una lista de entidades [Incidencia] a una lista de [IncidenciaResponse].
     *
     * @param incidencias La lista de entidades [Incidencia] a convertir.
     * @return Una lista de [IncidenciaResponse].
     * @author Natalia González Álvarez
     */
    fun toIncidenciaResponseList(incidencias: List<Incidencia>): List<IncidenciaResponse> {
        return incidencias.map { toIncidenciaResponse(it) }
    }
}