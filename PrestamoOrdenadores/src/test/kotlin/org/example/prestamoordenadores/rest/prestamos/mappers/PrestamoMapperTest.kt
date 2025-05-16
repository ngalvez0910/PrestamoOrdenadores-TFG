package org.example.prestamoordenadores.rest.prestamos.mappers

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class PrestamoMapperTest {
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

    private val prestamo = Prestamo(
        1,
        "guidTest123",
        user,
        dispositivo,
        EstadoPrestamo.EN_CURSO,
        LocalDate.now(),
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val mapper = PrestamoMapper()

    @Test
    fun toPrestamoResponse() {
        val userResponse = UserResponse(
            "2020LT475",
            "guidTest123",
            "email",
            "name",
            "apellido",
            "curso",
            "tutor",
            "avatar"
        )

        val dispositivoResponse = DispositivoResponse(
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
        )

        val prestamoResponse = PrestamoResponse(
            "guidTest123",
            userResponse,
            dispositivoResponse,
            "EN_CURSO",
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
    fun toPrestamoResponseAdmin() {
        val userResponse = UserResponse(
            "2020LT475",
            "guidTest123",
            "email",
            "name",
            "apellido",
            "curso",
            "tutor",
            "avatar"
        )

        val dispositivoResponse = DispositivoResponse(
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
        )

        val prestamoResponse = PrestamoResponseAdmin(
            "guidTest123",
            userResponse,
            dispositivoResponse,
            "EN_CURSO",
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            false
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
        val response = mapper.toPrestamoFromCreate(user, dispositivo)

        assertAll(
            { assertEquals(user, response.user) },
            { assertEquals(dispositivo, response.dispositivo) }
        )
    }

    @Test
    fun toPrestamoResponseList() {
        val prestamoResponseList = listOf(prestamo)

        val responseList = mapper.toPrestamoResponseList(prestamoResponseList)

        assertAll(
            { assertEquals(prestamoResponseList.size, responseList.size) },
            { assertEquals(prestamoResponseList[0].guid, responseList[0].guid) },
            { assertEquals(prestamoResponseList[0].user.guid, responseList[0].user.guid) },
            { assertEquals(prestamoResponseList[0].dispositivo.guid, responseList[0].dispositivo.guid) }
        )
    }

    @Test
    fun `toPrestamoResponseList lista vacia`() {
        val prestamos = emptyList<Prestamo>()

        val responses = mapper.toPrestamoResponseList(prestamos)

        assertTrue(responses.isEmpty())
    }
}