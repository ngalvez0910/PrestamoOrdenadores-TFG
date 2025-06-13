package org.example.prestamoordenadores.rest.sanciones.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.sanciones.dto.SancionAdminResponse
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
import org.example.prestamoordenadores.utils.emails.EmailService
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.util.ReflectionTestUtils
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
    lateinit var emailService: EmailService

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

    @BeforeEach
    fun setUp() {
        user = User(
            id = 99,
            guid = "guidTestU99",
            email = "email99.loantech@gmail.com",
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
            webService,
            emailService
        )
    }

    @Test
    fun getAllSanciones() {
        val sanciones = listOf(sancion)
        val pagedResponse = PagedResponse(
            content = listOf(response),
            totalElements = 1
        )

        every { repository.findAll(PageRequest.of(0, 10)) } returns PageImpl(sanciones)
        every { mapper.toSancionResponseList(sanciones) } returns listOf(response)

        val result = service.getAllSanciones(0, 10)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(pagedResponse, result.value) },
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
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { repository.save(any()) } returns sancion
        every { mapper.toSancionResponse(any()) } returns response
        every { updateRequest.tipoSancion } returns "bloqueo_temporal"
        every { webService.createAndSendNotification(any(), any()) } just Runs

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
        every { repository.save(sancion) } returns sancion
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { mapper.toSancionAdminResponse(any()) } returns responseAdmin
        every { webService.createAndSendNotification(any(), any()) } just Runs

        val result = service.deleteSancionByGuid("SANC0001")

        assertAll(
            { assertTrue(result.isOk) },
            { verify { repository.findByGuid("SANC0001") } },
            { verify { repository.save(sancion) } },
            { verify { mapper.toSancionAdminResponse(any()) } },
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
        val ahora = LocalDateTime.now()
        val prestamoVencido = Prestamo(
            guid = "PREST000001",
            user = user,
            estadoPrestamo = EstadoPrestamo.VENCIDO,
            updatedDate = ahora.minusDays(4)
        )

        every { prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO) } returns listOf(prestamoVencido)
        every { repository.existsByPrestamoGuidAndTipoSancion("PREST000001", TipoSancion.ADVERTENCIA) } returns false
        every { repository.save(any()) } returns sancion
        every { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) } returns emptyList()
        every { repository.findByUserAndTipoSancion(user, TipoSancion.ADVERTENCIA) } returns listOf(sancion)
        every { webService.createAndSendNotification(any(), any()) } just Runs
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { emailService.sendHtmlEmailSancion(any(), any(), any(), any()) } just Runs

        service.gestionarAdvertencias()

        verify(exactly = 1) { prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO) }
        verify(exactly = 1) { repository.existsByPrestamoGuidAndTipoSancion("PREST000001", TipoSancion.ADVERTENCIA) }
        verify(exactly = 1) { repository.save(match { it.tipoSancion == TipoSancion.ADVERTENCIA && it.prestamo == prestamoVencido }) }
        verify(exactly = 1) { emailService.sendHtmlEmailSancion(any(), any(), any(), any()) }
    }

    @Test
    fun `gestionarAdvertencias no encuentra préstamos vencidos`() {
        every { prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO) } returns emptyList()

        service.gestionarAdvertencias()

        verify(exactly = 1) { prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO) }
        verify(exactly = 0) { repository.existsByPrestamoGuidAndTipoSancion(any(), any()) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `gestionarAdvertencias encuentra préstamos vencidos pero no necesita crear advertencias`() {
        val ahora = LocalDateTime.now()
        val prestamoVencido = Prestamo(
            guid = "PREST000001",
            user = user,
            estadoPrestamo = EstadoPrestamo.VENCIDO,
            updatedDate = ahora.minusDays(2)
        )

        every { prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO) } returns listOf(prestamoVencido)

        service.gestionarAdvertencias()

        verify(exactly = 1) { prestamoRepository.findPrestamoByEstadoPrestamo(EstadoPrestamo.VENCIDO) }
        verify(exactly = 0) { repository.existsByPrestamoGuidAndTipoSancion(any(), any()) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun gestionarReactivacionYPosibleEscaladaAIndefinido() {
        val hoy = LocalDate.now()
        val userBloqueado = user.apply { isActivo = false }
        val sancionExpirada = sancion.apply {
            tipoSancion = TipoSancion.BLOQUEO_TEMPORAL
            fechaFin = hoy.minusDays(1)
            user = userBloqueado
        }

        every { repository.findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(
            TipoSancion.BLOQUEO_TEMPORAL, hoy
        ) } returns listOf(sancionExpirada)
        every { repository.findSancionsByUserAndTipoSancionIn(
            userBloqueado, listOf(TipoSancion.BLOQUEO_TEMPORAL, TipoSancion.INDEFINIDO)
        ) } returns listOf(sancionExpirada)
        every { userRepository.save(any()) } returns userBloqueado
        every { repository.findByUserAndTipoSancion(any(), TipoSancion.INDEFINIDO) } returns emptyList()
        every { repository.findByUserAndTipoSancion(any(), TipoSancion.BLOQUEO_TEMPORAL) } returns listOf(sancionExpirada)
        every { webService.createAndSendNotification(any(), any()) } just Runs
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { emailService.sendHtmlEmailUsuarioReactivado(any(), any(), any()) } just Runs

        service.gestionarReactivacionYPosibleEscaladaAIndefinido()

        verify(exactly = 1) { repository.findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(any(), any()) }
        verify(exactly = 1) { repository.findSancionsByUserAndTipoSancionIn(any(), any()) }
        verify(exactly = 1) { userRepository.save(match { it.isActivo }) }
        verify(exactly = 1) { emailService.sendHtmlEmailUsuarioReactivado(any(), any(), any()) }
    }

    @Test
    fun `gestionarReactivacionYPosibleEscaladaAIndefinido no encuentra sanciones de bloqueo expiradas`() {
        val hoy = LocalDate.now()
        every { repository.findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(
            TipoSancion.BLOQUEO_TEMPORAL, hoy
        ) } returns emptyList()

        service.gestionarReactivacionYPosibleEscaladaAIndefinido()

        verify(exactly = 1) { repository.findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(any(), any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun evaluarPasoAIndefinido_NoEscalaSiYaTieneUnIndefinido() {
        val hoy = LocalDate.now()
        val sancionIndefinidaActiva = sancion.apply {
            tipoSancion = TipoSancion.INDEFINIDO
            fechaSancion = hoy
            fechaFin = null
        }

        every { repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO) } returns
                listOf(sancionIndefinidaActiva)

        // When
        service.evaluarPasoAIndefinido(user)

        // Then
        verify(exactly = 1) { repository.findByUserAndTipoSancion(user, TipoSancion.INDEFINIDO) }
        verify(exactly = 0) { repository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL) }
        verify(exactly = 0) { repository.save(any()) }
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
    fun crearBloqueoTemporal() {
        val prestamo = mockk<Prestamo>()

        every { userRepository.save(user) } returns user
        every { repository.save(any()) } answers { firstArg() }
        every { webService.createAndSendNotification(any(), any()) } just Runs
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { emailService.sendHtmlEmailSancion(any(), any(), any(), any()) } just Runs

        service.crearBloqueoTemporal(user, "motivo de test", prestamo)

        assertFalse(user.isActivo)
        verify { userRepository.save(user) }
        verify { repository.save(match { it.tipoSancion == TipoSancion.BLOQUEO_TEMPORAL }) }
        verify { webService.createAndSendNotification(any(), any()) }
        verify { userRepository.findUsersByRol(any()) }
        verify { emailService.sendHtmlEmailSancion(any(), any(), any(), any()) }
    }

    @Test
    fun crearBloqueoIndefinido() {
        every { userRepository.save(user) } returns user
        every { repository.save(any()) } answers { firstArg() }
        every { webService.createAndSendNotification(any(), any()) } just Runs
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { emailService.sendHtmlEmailSancion(any(), any(), any(), any()) } just Runs

        service.crearBloqueoIndefinido(user, "motivo indefinido")

        assertFalse(user.isActivo)
        verify { userRepository.save(user) }
        verify { repository.save(match { it.tipoSancion == TipoSancion.INDEFINIDO }) }
        verify { webService.createAndSendNotification(any(), any()) }
        verify { userRepository.findUsersByRol(any()) }
        verify { emailService.sendHtmlEmailSancion(any(), any(), any(), any()) }
    }
}