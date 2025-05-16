package org.example.prestamoordenadores.rest.incidencias.mappers

import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponseAdmin
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class IncidenciaMapperTest {
    private val user = User(
        1,
        "guidTest123",
        "email",
        "password",
        Role.ALUMNO,
        "numIdent",
        "name",
        "apellido",
        "curso",
        "tutor",
        "avatar",
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val incidencia = Incidencia(
        1,
        "guidTest123",
        "Asunto",
        "Descripcion",
        EstadoIncidencia.PENDIENTE,
        user,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val mapper = IncidenciaMapper()

    @Test
    fun toIncidenciaResponse() {
        val userResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )

        val incidenciaResponse = IncidenciaResponse(
            "guidTest123",
            "Asunto",
            "Descripcion",
            EstadoIncidencia.PENDIENTE.toString(),
            userResponse,
            LocalDate.now().toString()
        )

        val response = mapper.toIncidenciaResponse(incidencia)

        assertAll(
            { assertEquals(incidenciaResponse.guid, response.guid) },
            { assertEquals(incidenciaResponse.asunto, response.asunto) },
            { assertEquals(incidenciaResponse.descripcion, response.descripcion) },
            { assertEquals(incidenciaResponse.estadoIncidencia, response.estadoIncidencia) },
            { assertEquals(incidenciaResponse.user, response.user) }
        )
    }

    @Test
    fun toIncidenciaResponseAdmin() {
        val userResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )

        val incidenciaResponse = IncidenciaResponseAdmin(
            "guidTest123",
            "Asunto",
            "Descripcion",
            EstadoIncidencia.PENDIENTE.toString(),
            userResponse,
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            false
        )

        val response = mapper.toIncidenciaResponse(incidencia)

        assertAll(
            { assertEquals(incidenciaResponse.guid, response.guid) },
            { assertEquals(incidenciaResponse.asunto, response.asunto) },
            { assertEquals(incidenciaResponse.descripcion, response.descripcion) },
            { assertEquals(incidenciaResponse.estadoIncidencia, response.estadoIncidencia) },
            { assertEquals(incidenciaResponse.user, response.user) }
        )
    }

    @Test
    fun toIncidenciaFromCreate() {
        val incidenciaCreate = IncidenciaCreateRequest(
            "Asunto",
            "Descripcion"
        )

        val response = mapper.toIncidenciaFromCreate(incidenciaCreate, user)

        assertAll(
            { assertEquals(incidenciaCreate.asunto, response.asunto) },
            { assertEquals(incidenciaCreate.descripcion, response.descripcion) }
        )
    }

    @Test
    fun toIncidenciaResponseList() {
        val incidenciaList = listOf(incidencia)

        val response = mapper.toIncidenciaResponseList(incidenciaList)

        assertAll(
            { assertEquals(incidenciaList[0].guid, response[0].guid) },
            { assertEquals(incidenciaList[0].asunto, response[0].asunto) },
            { assertEquals(incidenciaList[0].descripcion, response[0].descripcion) },
            { assertEquals(incidenciaList[0].estadoIncidencia.toString(), response[0].estadoIncidencia) },
            { assertEquals(incidenciaList[0].user.guid, response[0].user.guid) }
        )
    }

    @Test
    fun `toIncidenciaResponseList lista vacia`() {
        val incidencias = emptyList<Incidencia>()

        val responses = mapper.toIncidenciaResponseList(incidencias)

        assertTrue(responses.isEmpty())
    }
}