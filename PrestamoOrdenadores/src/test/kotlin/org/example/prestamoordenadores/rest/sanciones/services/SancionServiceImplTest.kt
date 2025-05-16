package org.example.prestamoordenadores.rest.sanciones.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.example.prestamoordenadores.config.websockets.WebSocketHandler
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.sanciones.dto.SancionAdminResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.example.prestamoordenadores.rest.sanciones.mappers.SancionMapper
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.validators.validate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class SancionServiceImplTest {

    @MockK
    lateinit var repository: SancionRepository

    @MockK
    lateinit var mapper: SancionMapper

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var prestamoRepository: PrestamoRepository

    @MockK
    lateinit var webService: WebSocketService

    @MockK
    lateinit var createRequest: SancionRequest

    @MockK
    lateinit var updateRequest: SancionUpdateRequest

    lateinit var service: SancionServiceImpl

    var user = User()
    var sancion = Sancion()
    var userResponse = UserResponse(
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
    var response = SancionResponse(
        guid = sancion.guid,
        user = userResponse,
        prestamo = prestamoResponse,
        tipoSancion = sancion.tipoSancion.toString(),
        fechaSancion = sancion.fechaSancion.toString(),
        fechaFin = sancion.fechaFin.toString(),
    )

    @BeforeEach
    fun setUp() {
        user = User(
            id = 99,
            guid = "guidTestU99",
            email = "email99@loantech.com",
            numeroIdentificacion = "2023LT249",
            campoPassword = "Password123?",
            nombre = "nombre99",
            apellidos = "apellidos99",
            curso = "curso99",
            tutor = "tutor99",
            avatar = "avatar99.png",
            rol = Role.ALUMNO,
            isActivo = true,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        sancion = Sancion(
            id = 1,
            guid = "SANC000000",
            user = user,
            tipoSancion = TipoSancion.ADVERTENCIA,
            fechaSancion = LocalDate.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        service = SancionServiceImpl(
            repository,
            mapper,
            userRepository,
            prestamoRepository,
            webService
        )
    }

    @Test
    fun getAllSanciones() {
        val sanciones = listOf(sancion)
        every { repository.findAll(PageRequest.of(0, 10)) } returns PageImpl(sanciones)
        every { mapper.toSancionResponseList(sanciones) } returns listOf(response)

        val result = service.getAllSanciones(0, 10)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(listOf(response), result.value) },
            { verify { repository.findAll(PageRequest.of(0, 10)) } },
            { verify { mapper.toSancionResponseList(sanciones) } }
        )
    }

    @Test
    fun getSancionByGuid() {
        every { repository.findByGuid(sancion.guid) } returns sancion
        every { mapper.toSancionResponse(sancion) } returns response

        val result = service.getSancionByGuid(sancion.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { repository.findByGuid(sancion.guid) } },
            { verify { mapper.toSancionResponse(sancion) } }
        )
    }

    @Test
    fun `getSancionByGuid returns Err when sancion no existe`() {
        every { repository.findByGuid("sancion-guid") } returns null

        val result = service.getSancionByGuid("sancion-guid")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.SancionNotFound) },
            { verify { repository.findByGuid("sancion-guid") } }
        )
    }

    @Test
    fun getSancionByGuidAdmin() {
        var responseAdmin = SancionAdminResponse(
            guid = sancion.guid,
            user = userResponse,
            prestamo = prestamoResponse,
            tipoSancion = sancion.tipoSancion.toString(),
            fechaSancion = sancion.fechaSancion.toString(),
            fechaFin = sancion.fechaFin.toString(),
            createdDate = sancion.createdDate.toString(),
            updatedDate = sancion.updatedDate.toString(),
            isDeleted = sancion.isDeleted
        )

        every { repository.findByGuid(sancion.guid) } returns sancion
        every { mapper.toSancionAdminResponse(sancion) } returns responseAdmin

        val result = service.getSancionByGuidAdmin(sancion.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(responseAdmin, result.value) },
            { verify { repository.findByGuid(sancion.guid) } },
            { verify { mapper.toSancionAdminResponse(sancion) } }
        )
    }

    @Test
    fun `getSancionByGuidAdmin returns Err when sancion no existe`() {
        every { repository.findByGuid("sancion-guid") } returns null

        val result = service.getSancionByGuidAdmin("sancion-guid")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.SancionNotFound) },
            { verify { repository.findByGuid("sancion-guid") } }
        )
    }

    @Test
    fun updateSancion() {
        every { repository.findByGuid(sancion.guid) } returns sancion
        every { updateRequest.validate() } returns Ok(updateRequest)
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin@loantech.com", rol = Role.ADMIN))
        every { repository.save(any()) } returns sancion
        every { mapper.toSancionResponse(any()) } returns response
        every { updateRequest.tipoSancion } returns "bloqueo_temporal"

        val result = service.updateSancion(sancion.guid, updateRequest)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { repository.findByGuid(sancion.guid) } },
            { verify { updateRequest.validate() } },
            { verify { repository.save(any()) } },
            { verify { mapper.toSancionResponse(any()) } },
        )
    }

    @Test
    fun `updateSancion returns Err when sancion invalida`() {
        val request = SancionUpdateRequest(tipoSancion = "")

        mockkStatic("org.example.prestamoordenadores.utils.validators.SancionValidatorKt")
        every { request.validate() } returns Err(SancionError.SancionValidationError("Invalid"))

        val result = service.updateSancion(sancion.guid, request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.SancionValidationError) },
            { verify { request.validate() } }
        )
    }

    @Test
    fun `updateSancion  returns Err when sancion no existe`() {
        every { repository.findByGuid("sancion-guid") } returns null
        every { updateRequest.tipoSancion } returns "BLOQUEO_TEMPORAL"

        val result = service.updateSancion("sancion-guid", updateRequest)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.SancionNotFound) }
        )
    }

    @Test
    fun deleteSancionByGuid() {
        every { repository.findByGuid("SANC0001") } returns sancion
        every { repository.delete(sancion) } just Runs
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin@loantech.com", rol = Role.ADMIN))
        every { mapper.toSancionResponse(any()) } returns response

        val result = service.deleteSancionByGuid("SANC0001")

        assertAll(
            { assertTrue(result.isOk) },
            { verify { repository.findByGuid("SANC0001") } },
            { verify { repository.delete(sancion) } },
            { verify { mapper.toSancionResponse(any()) } },
        )
    }

    @Test
    fun `deleteSancionByGuid returns Err when sancion no existe`() {
        every { repository.findByGuid("sancion-guid") } returns null

        val result = service.deleteSancionByGuid("sancion-guid")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.SancionNotFound) }
        )
    }

    @Test
    fun getByFecha() {
        val sanciones = listOf(sancion)
        every { repository.findSancionByFechaSancion(sancion.fechaSancion) } returns sanciones
        every { mapper.toSancionResponseList(sanciones) } returns listOf(response)

        val result = service.getByFecha(sancion.fechaSancion)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(listOf(response), result.value) },
            { verify { repository.findSancionByFechaSancion(sancion.fechaSancion) } },
            { verify { mapper.toSancionResponseList(sanciones) } }
        )
    }

    @Test
    fun getByTipo() {
        val sanciones = listOf(sancion)
        val expectedResponse = listOf(response)

        every { repository.findSancionByTipoSancion(TipoSancion.ADVERTENCIA) } returns sanciones
        every { mapper.toSancionResponseList(sanciones) } returns expectedResponse

        val result = service.getByTipo(TipoSancion.ADVERTENCIA.toString())

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findSancionByTipoSancion(TipoSancion.ADVERTENCIA) } },
            { verify { mapper.toSancionResponseList(sanciones) } }
        )
    }

    @Test
    fun `getByTipo returns Err when tipo no existe`() {
        val result = service.getByTipo("invalido")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.SancionNotFound) }
        )
    }

    @Test
    fun getSancionByUserGuid() {
        val sanciones = listOf(sancion)
        val responseList = listOf(response)

        every { userRepository.findByGuid(user.guid) } returns user
        every { repository.findByUserGuid(user.guid) } returns sanciones
        every { mapper.toSancionResponseList(sanciones) } returns responseList

        val result = service.getSancionByUserGuid(user.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(responseList, result.value) },
            { verify { userRepository.findByGuid(user.guid) } },
            { verify { repository.findByUserGuid(user.guid) } },
            { verify { mapper.toSancionResponseList(sanciones) } }
        )
    }

    @Test
    fun `getSancionByUserGuid returns Err when user no existe`() {
        every { userRepository.findByGuid("user-guid") } returns null

        val result = service.getSancionByUserGuid("user-guid")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.UserNotFound) },
            { verify { userRepository.findByGuid("user-guid") } }
        )
    }

    @Test
    fun gestionarAdvertencias() {
        val dispositivo = Dispositivo(
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
        val vencidoHace4Dias = LocalDateTime.now().minusDays(4)
        val prestamo = Prestamo(
            1,
            "guidTest123",
            user,
            dispositivo,
            EstadoPrestamo.VENCIDO,
            LocalDate.now(),
            LocalDate.now(),
            LocalDateTime.now(),
            vencidoHace4Dias
        )

        every { prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO) } returns listOf(prestamo)
        every { repository.existsByPrestamoGuidAndTipoSancion(prestamo.guid, TipoSancion.ADVERTENCIA) } returns false
        every { repository.save(any()) } returns Sancion(user = user, tipoSancion = TipoSancion.ADVERTENCIA, prestamo = prestamo)
        every { service.evaluarPasoABloqueo(user) } just Runs

        service.gestionarAdvertencias()

        verify { repository.save(match { it.tipoSancion == TipoSancion.ADVERTENCIA && it.prestamo == prestamo }) }
        verify { service.evaluarPasoABloqueo(user) }
    }

    @Test
    fun gestionarReactivacionYPosibleEscaladaAIndefinido() {
        val sancionExpirada = Sancion(
            guid = "sancion-guid",
            user = user,
            tipoSancion = TipoSancion.BLOQUEO_TEMPORAL,
            fechaFin = LocalDate.now().minusDays(1)
        )

        every {
            repository.findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(
                TipoSancion.BLOQUEO_TEMPORAL, any()
            )
        } returns listOf(sancionExpirada)

        every {
            repository.findSancionsByUserAndTipoSancionIn(user, listOf(TipoSancion.BLOQUEO_TEMPORAL, TipoSancion.INDEFINIDO))
        } returns listOf(sancionExpirada)

        every { userRepository.save(user) } returns user
        every { service.evaluarPasoAIndefinido(user) } just Runs

        service.gestionarReactivacionYPosibleEscaladaAIndefinido()

        verify { userRepository.save(match { it.isActivo }) }
        verify { service.evaluarPasoAIndefinido(user) }
    }

    @Test
    fun evaluarPasoAIndefinido() {
        val bloqueo1 = Sancion(fechaFin = LocalDate.now().minusDays(5))
        val bloqueo2 = Sancion(fechaFin = LocalDate.now().minusDays(10))

        every { repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO) } returns emptyList()
        every { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) } returns listOf(bloqueo1, bloqueo2)
        every { repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO) } returns emptyList()
        every { service.crearBloqueoIndefinido(user, any()) } just Runs

        service.evaluarPasoAIndefinido(user)

        verify { service.crearBloqueoIndefinido(user, match { it.contains("2 bloqueos") }) }
    }

    @Test
    fun evaluarPasoAIndefinido_NoEscalaSiYaTieneUnaActiva() {
        val sancionActiva = mockk<Sancion>()
        every { sancionActiva.isActiveNow() } returns true

        every { repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO) } returns listOf(sancionActiva)

        service.evaluarPasoAIndefinido(user)

        verify(exactly = 0) { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) }
        verify(exactly = 0) { service.crearBloqueoIndefinido(any(), any()) }
    }

    @Test
    fun evaluarPasoAIndefinido_NoEscalaSiYaTieneUnIndefinido() {
        val indefinidaHoy = Sancion(fechaSancion = LocalDate.now())
        val bloqueo1 = Sancion(fechaFin = LocalDate.now().minusDays(10))
        val bloqueo2 = Sancion(fechaFin = LocalDate.now().minusDays(20))

        every { repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO) } returns listOf(indefinidaHoy)
        every { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) } returns listOf(bloqueo1, bloqueo2)

        service.evaluarPasoAIndefinido(user)

        verify(exactly = 0) { service.crearBloqueoIndefinido(any(), any()) }
    }

    @Test
    fun evaluarPasoABloqueo() {
        val prestamo = mockk<Prestamo>()
        val adv1 = Sancion(fechaSancion = LocalDate.now(), prestamo = prestamo)
        val adv2 = Sancion(fechaSancion = LocalDate.now().minusDays(1), prestamo = prestamo)

        every { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) } returns emptyList()
        every { repository.findByUserAndTipoSancion(user, TipoSancion.ADVERTENCIA) } returns listOf(adv1, adv2)
        every { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) } returns emptyList()

        every { service.crearBloqueoTemporal(user, any(), prestamo) } just Runs

        service.evaluarPasoABloqueo(user)

        verify { service.crearBloqueoTemporal(user, match { it.contains("advertencias") }, prestamo) }
    }

    @Test
    fun evaluarPasoABloqueo_NoEscalaSiYaTieneUnActivo() {
        val bloqueoActivo = mockk<Sancion>()
        every { bloqueoActivo.isActiveNow() } returns true

        every { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) } returns listOf(bloqueoActivo)

        service.evaluarPasoABloqueo(user)

        verify(exactly = 0) { repository.findByUserAndTipoSancion(user, TipoSancion.ADVERTENCIA) }
    }

    @Test
    fun evaluarPasoABloqueo_NoEscalaSiTieneMenosDeDosAdvertencias() {
        val advertencia = Sancion(fechaSancion = LocalDate.now(), prestamo = mockk())

        every { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) } returns emptyList()
        every { repository.findByUserAndTipoSancion(user, TipoSancion.ADVERTENCIA) } returns listOf(advertencia)

        service.evaluarPasoABloqueo(user)

        verify(exactly = 0) { service.crearBloqueoTemporal(any(), any(), any()) }
    }

    @Test
    fun crearBloqueoTemporal() {
        val prestamo = mockk<Prestamo>()

        every { userRepository.save(user) } returns user
        every { repository.save(any()) } returns mockk()

        service.crearBloqueoTemporal(user, "motivo de test", prestamo)

        assertFalse(user.isActivo)
        verify { userRepository.save(user) }
        verify { repository.save(match { it.tipoSancion == TipoSancion.BLOQUEO_TEMPORAL }) }
    }

    @Test
    fun crearBloqueoIndefinido() {
        every { userRepository.save(user) } returns user
        every { repository.save(any()) } returns mockk()

        service.crearBloqueoIndefinido(user, "motivo indefinido")

        assertFalse(user.isActivo)
        verify { userRepository.save(user) }
        verify { repository.save(match { it.tipoSancion == TipoSancion.INDEFINIDO }) }
    }
}