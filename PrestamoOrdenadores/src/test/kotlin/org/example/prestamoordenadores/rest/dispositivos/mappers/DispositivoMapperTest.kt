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
        "",
        20,
        true,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val mapper = DispositivoMapper()

    @Test
    fun toDispositivoResponse() {
        val dispositivoResponse = DispositivoResponse(
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
            20,
        )

        val response = mapper.toDispositivoResponse(dispositivo)

        assertAll(
            { assertEquals(dispositivoResponse.guid, response.guid) },
            { assertEquals(dispositivoResponse.numeroSerie, response.numeroSerie) },
            { assertEquals(dispositivoResponse.componentes, response.componentes) },
            { assertEquals(dispositivoResponse.stock, response.stock) }
        )
    }

    @Test
    fun toDispositivoFromCreate() {
        val dispositivoCreate = DispositivoCreateRequest(
            "raton, cargador",
            20,
        )

        val response = mapper.toDispositivoFromCreate(dispositivoCreate)

        assertAll(
            { assertEquals(dispositivoCreate.componentes, response.componentes) },
            { assertEquals(dispositivoCreate.stock, response.stock) }
        )
    }

    @Test
    fun toDispositivoResponseAdmin() {
        val dispositivoResponseAdmin = DispositivoResponseAdmin(
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
            EstadoDispositivo.DISPONIBLE.toString(),
            "",
            20,
            true
        )

        val response = mapper.toDispositivoResponseAdmin(dispositivo)

        assertAll(
            { assertEquals(dispositivoResponseAdmin.guid, response.guid) },
            { assertEquals(dispositivoResponseAdmin.numeroSerie, response.numeroSerie) },
            { assertEquals(dispositivoResponseAdmin.componentes, response.componentes) },
            { assertEquals(dispositivoResponseAdmin.estado, response.estado) },
            { assertEquals(dispositivoResponseAdmin.incidenciaGuid, response.incidenciaGuid) },
            { assertEquals(dispositivoResponseAdmin.stock, response.stock) },
            { assertEquals(dispositivoResponseAdmin.isActivo, response.isActivo) }
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
            { assertEquals("", responses[0].incidenciaGuid) }
        )
    }

    @Test
    fun `toDispositivoResponseListAdmin lista vacia`() {
        val dispositivos = emptyList<Dispositivo>()

        val responses = mapper.toDispositivoResponseListAdmin(dispositivos)

        assertTrue(responses.isEmpty())
    }
}