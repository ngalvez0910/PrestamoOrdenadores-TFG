package org.example.prestamoordenadores.rest.incidencias.mappers

import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class IncidenciaMapperTest {
    private val incidencia = Incidencia(
        1,
        "guidTest123",
        "Asunto",
        "Descripcion",
        EstadoIncidencia.PENDIENTE,
        "userGuid",
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val mapper = IncidenciaMapper()

    @Test
    fun toIncidenciaResponse() {
        val incidenciaResponse = IncidenciaResponse(
            "guidTest123",
            "Asunto",
            "Descripcion",
            EstadoIncidencia.PENDIENTE.toString(),
            "userGuid",
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
    fun toIncidenciaFromCreate() {
        val incidenciaCreate = IncidenciaCreateRequest(
            "Asunto",
            "Descripcion"
        )

        val response = mapper.toIncidenciaFromCreate(incidenciaCreate)

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
            { assertEquals(incidenciaList[0].userGuid, response[0].user) }
        )
    }

    @Test
    fun `toIncidenciaResponseList lista vacia`() {
        val incidencias = emptyList<Incidencia>()

        val responses = mapper.toIncidenciaResponseList(incidencias)

        assertTrue(responses.isEmpty())
    }
}