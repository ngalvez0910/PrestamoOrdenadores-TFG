package org.example.prestamoordenadores.rest.dispositivos.mappers

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DispositivoMapperTest {
    private val dispositivo = Dispositivo(
        1,
        "guidTest123",
        "5CD1234XYZ",
        "raton, cargador",
        EstadoDispositivo.DISPONIBLE,
        null,
        LocalDateTime.now(),
        LocalDateTime.now(),
        false
    )

    private val mapper = DispositivoMapper()

    @Test
    fun toDispositivoResponse() {
        val dispositivoResponse = DispositivoResponse(
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
        )

        val response = mapper.toDispositivoResponse(dispositivo)

        assertAll(
            { assertEquals(dispositivoResponse.guid, response.guid) },
            { assertEquals(dispositivoResponse.numeroSerie, response.numeroSerie) },
            { assertEquals(dispositivoResponse.componentes, response.componentes) },
        )
    }

    @Test
    fun toDispositivoFromCreate() {
        val dispositivoCreate = DispositivoCreateRequest(
            "0DW9406KDF",
            "raton, cargador",
        )

        val response = mapper.toDispositivoFromCreate(dispositivoCreate)

        assertAll(
            { assertEquals(dispositivoCreate.componentes, response.componentes) },
        )
    }

    @Test
    fun toDispositivoResponseAdmin() {
        val dispositivoResponseAdmin = DispositivoResponseAdmin(
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
            EstadoDispositivo.DISPONIBLE.toString(),
            null,
            true
        )

        val response = mapper.toDispositivoResponseAdmin(dispositivo)

        assertAll(
            { assertEquals(dispositivoResponseAdmin.guid, response.guid) },
            { assertEquals(dispositivoResponseAdmin.numeroSerie, response.numeroSerie) },
            { assertEquals(dispositivoResponseAdmin.componentes, response.componentes) },
            { assertEquals(dispositivoResponseAdmin.estado, response.estado) },
            { assertEquals(dispositivoResponseAdmin.incidencia, response.incidencia) },
        )
    }

    @Test
    fun toDispositivoResponseListAdmin() {
        val dispositivos = listOf(dispositivo)

        val responses = mapper.toDispositivoResponseListAdmin(dispositivos)

        assertAll(
            { assertEquals(1, responses.size) },
            { assertEquals("guidTest123", responses[0].guid) },
            { assertEquals("5CD1234XYZ", responses[0].numeroSerie) },
            { assertEquals("DISPONIBLE", responses[0].estado) },
            { assertEquals(null, responses[0].incidencia) }
        )
    }

    @Test
    fun `toDispositivoResponseListAdmin lista vacia`() {
        val dispositivos = emptyList<Dispositivo>()

        val responses = mapper.toDispositivoResponseListAdmin(dispositivos)

        assertTrue(responses.isEmpty())
    }
}