package org.example.prestamoordenadores.rest.sanciones.mappers

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.sanciones.dto.SancionAdminResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.LocalDate
import java.time.LocalDateTime

class SancionMapperTest {
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

    private val sancion = Sancion(
        1L,
        "guidTest123",
        user,
        prestamo,
        TipoSancion.ADVERTENCIA,
        LocalDate.now(),
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        false
    )

    private val userRepository = mock(UserRepository::class.java)
    private val mapper = SancionMapper()

    @Test
    fun toSancionResponse() {
        val userResponse = UserResponse(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!,
            avatar = user.avatar
        )

        val dispositivoResponse = DispositivoResponse(
            guid = "guidTestD02",
            numeroSerie = "4JT8695OPQ",
            componentes = "cargador"
        )

        val prestamoResponse = PrestamoResponse(
            guid = prestamo.guid,
            user = userResponse,
            dispositivo = dispositivoResponse,
            estadoPrestamo = prestamo.estadoPrestamo.toString(),
            fechaPrestamo = prestamo.fechaPrestamo.toString(),
            fechaDevolucion = prestamo.fechaDevolucion.toString()
        )

        val sancionResponse = SancionResponse(
            "guidTest123",
            userResponse,
            prestamoResponse,
            TipoSancion.ADVERTENCIA.toString(),
            LocalDate.now().toString(),
            LocalDate.now().toString()
        )

        val response = mapper.toSancionResponse(sancion)

        assertAll(
            { assertEquals(sancionResponse.guid, response.guid) },
            { assertEquals(sancionResponse.user.guid, response.user.guid) },
            { assertEquals(sancionResponse.tipoSancion, response.tipoSancion.toString()) }
        )
    }

    @Test
    fun toSancionResponseAdmin() {
        val userResponse = UserResponse(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!,
            avatar = user.avatar
        )

        val dispositivoResponse = DispositivoResponse(
            guid = "guidTestD02",
            numeroSerie = "4JT8695OPQ",
            componentes = "cargador"
        )

        val prestamoResponse = PrestamoResponse(
            guid = prestamo.guid,
            user = userResponse,
            dispositivo = dispositivoResponse,
            estadoPrestamo = prestamo.estadoPrestamo.toString(),
            fechaPrestamo = prestamo.fechaPrestamo.toString(),
            fechaDevolucion = prestamo.fechaDevolucion.toString()
        )

        val sancionResponse = SancionAdminResponse(
            "guidTest123",
            userResponse,
            prestamoResponse,
            TipoSancion.ADVERTENCIA.toString(),
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            false
        )

        val response = mapper.toSancionResponse(sancion)

        assertAll(
            { assertEquals(sancionResponse.guid, response.guid) },
            { assertEquals(sancionResponse.user.guid, response.user.guid) },
            { assertEquals(sancionResponse.tipoSancion, response.tipoSancion.toString()) }
        )
    }

    @Test
    fun toSancionFromRequest() {
        val request = SancionRequest(
            user.guid,
            "ADVERTENCIA"
        )

        `when`(userRepository.findByGuid(user.guid)).thenReturn(user)

        val result = mapper.toSancionFromRequest(request, userRepository)

        assertAll(
            { assertEquals(user, result.user) },
            { assertEquals(TipoSancion.ADVERTENCIA, result.tipoSancion) },
            { assertEquals(LocalDate.now(), result.fechaSancion) },
            { assertNotNull(result.createdDate) },
            { assertNotNull(result.updatedDate) }
        )
    }

    @Test
    fun toSancionResponseList() {
        val responses = mapper.toSancionResponseList(listOf(sancion))

        assertAll(
            { assertEquals(1, responses.size) },
            { assertEquals(sancion.guid, responses[0].guid) },
            { assertEquals(sancion.user.guid, responses[0].user.guid) },
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