package org.example.prestamoordenadores.rest.sanciones.mappers

import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class SancionMapperTest {
    private val sancion = Sancion(
        1,
        "guidTest123",
        "userGuid",
        TipoSancion.ADVERTENCIA,
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val mapper = SancionMapper()

    @Test
    fun toSancionResponse() {
        val sancionResponse = SancionResponse(
            "guidTest123",
            "userGuid",
            TipoSancion.ADVERTENCIA.toString(),
            LocalDate.now().toString(),
        )

        val response = mapper.toSancionResponse(sancion)

        assertAll(
            { assertEquals(sancionResponse.guid, response.guid) },
            { assertEquals(sancionResponse.userGuid, response.userGuid) },
            { assertEquals(sancionResponse.tipoSancion, response.tipoSancion.toString()) }
        )
    }

    @Test
    fun toSancionFromRequest() {
        val sancionRequest = SancionRequest(
            "userGuid",
            TipoSancion.ADVERTENCIA.toString(),
        )

        val response = mapper.toSancionFromRequest(sancionRequest)

        assertAll(
            { assertEquals(sancionRequest.userGuid, response.userGuid) },
            { assertEquals(sancionRequest.tipoSancion, response.tipoSancion.toString()) }
        )
    }

    @Test
    fun toSancionResponseList() {
        val responses = mapper.toSancionResponseList(listOf(sancion))

        assertAll(
            { assertEquals(1, responses.size) },
            { assertEquals(sancion.guid, responses[0].guid) },
            { assertEquals(sancion.userGuid, responses[0].userGuid) },
            { assertEquals(sancion.tipoSancion.toString(), responses[0].tipoSancion) }
        )
    }

    @Test
    fun `toSancionResponseList lista vacia`() {
        val sanciones = emptyList<Sancion>()

        val responses = mapper.toSancionResponseList(sanciones)

        assertTrue(responses.isEmpty())
    }
}