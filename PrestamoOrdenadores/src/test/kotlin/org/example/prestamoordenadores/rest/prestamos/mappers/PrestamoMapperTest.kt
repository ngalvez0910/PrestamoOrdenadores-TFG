package org.example.prestamoordenadores.rest.prestamos.mappers

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class PrestamoMapperTest {
    private val prestamo = Prestamo(
        1,
        "guidTest123",
        "userGuid",
        "dispositivoGuid",
        EstadoPrestamo.EN_CURSO,
        LocalDate.now(),
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val mapper = PrestamoMapper()

    @Test
    fun toPrestamoResponse() {
        val prestamoResponse = PrestamoResponse(
            "guidTest123",
            "userGuid",
            "dispositivoGuid",
            LocalDate.now().toString(),
            LocalDate.now().toString(),
        )

        val response = mapper.toPrestamoResponse(prestamo)

        assertAll(
            { assertEquals(prestamoResponse.guid, response.guid) },
            { assertEquals(prestamoResponse.user, response.user) },
            { assertEquals(prestamoResponse.dispositivo, response.dispositivo) }
        )
    }

    @Test
    fun toPrestamoFromCreate() {
        val dispositivo = Dispositivo(
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

        val prestamoCreate = PrestamoCreateRequest(
            "userGuid"
        )

        val response = mapper.toPrestamoFromCreate(prestamoCreate, dispositivo)

        assertAll(
            { assertEquals(prestamoCreate.userGuid, response.user) }
        )
    }

    @Test
    fun toPrestamoResponseList() {
        val prestamoResponseList = listOf(prestamo)

        val responseList = mapper.toPrestamoResponseList(prestamoResponseList)

        assertAll(
            { assertEquals(prestamoResponseList.size, responseList.size) },
            { assertEquals(prestamoResponseList[0].guid, responseList[0].guid) },
            { assertEquals(prestamoResponseList[0].user, responseList[0].user) },
            { assertEquals(prestamoResponseList[0].dispositivo, responseList[0].dispositivo) }
        )
    }

    @Test
    fun `toPrestamoResponseList lista vacia`() {
        val prestamos = emptyList<Prestamo>()

        val responses = mapper.toPrestamoResponseList(prestamos)

        assertTrue(responses.isEmpty())
    }
}