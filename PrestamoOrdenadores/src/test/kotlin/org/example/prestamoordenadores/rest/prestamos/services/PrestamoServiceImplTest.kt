package org.example.prestamoordenadores.rest.prestamos.services

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
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.rest.prestamos.mappers.PrestamoMapper
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.storage.pdf.PrestamoPdfStorage
import org.example.prestamoordenadores.utils.emails.EmailService
import org.example.prestamoordenadores.utils.validators.validate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class PrestamoServiceImplTest {
    @MockK
    lateinit var repository: PrestamoRepository

    @MockK
    lateinit var mapper: PrestamoMapper

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var dispositivoRepository: DispositivoRepository

    @MockK
    lateinit var storage : PrestamoPdfStorage

    @MockK
    lateinit var webService: WebSocketService

    @MockK
    lateinit var emailService: EmailService

    @MockK
    lateinit var updateRequest: PrestamoUpdateRequest

    lateinit var service: PrestamoServiceImpl

    var user = User()
    var dispositivo = Dispositivo()
    var prestamo = Prestamo()
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
        guid = "guidTestD02",
        numeroSerie = "4JT8695OPQ",
        componentes = "cargador"
    )
    val response = PrestamoResponse(
        guid = prestamo.guid,
        user = userResponse,
        dispositivo = dispositivoResponse,
        estadoPrestamo = prestamo.estadoPrestamo.toString(),
        fechaPrestamo = prestamo.fechaPrestamo.toString(),
        fechaDevolucion = prestamo.fechaDevolucion.toString()
    )
    val adminResponse = PrestamoResponseAdmin(
        guid = prestamo.guid,
        user = userResponse,
        dispositivo = dispositivoResponse,
        estadoPrestamo = prestamo.estadoPrestamo.toString(),
        fechaPrestamo = prestamo.fechaPrestamo.toString(),
        fechaDevolucion = prestamo.fechaDevolucion.toString(),
        createdDate = prestamo.createdDate.toString(),
        updatedDate = prestamo.updatedDate.toString(),
        isDeleted = prestamo.isDeleted
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

        dispositivo = Dispositivo(
            1L,
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
            EstadoDispositivo.DISPONIBLE,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        )

        prestamo = Prestamo(
            id = 1,
            guid = "guidTestP01",
            user = user,
            dispositivo = dispositivo,
            estadoPrestamo = EstadoPrestamo.EN_CURSO,
            fechaPrestamo = LocalDate.now(),
            fechaDevolucion = LocalDate.now().plusDays(1),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        service = PrestamoServiceImpl(
            repository,
            mapper,
            userRepository,
            dispositivoRepository,
            storage,
            webService,
            emailService
        )
    }

    @Test
    fun getAllPrestamos() {
        val prestamos = listOf(prestamo)
        every { repository.findAll(PageRequest.of(0, 10)) } returns PageImpl(prestamos)
        every { mapper.toPrestamoResponseList(prestamos) } returns listOf(response)

        val result = service.getAllPrestamos(0, 10)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(listOf(response), result.value.content) },
            { verify { repository.findAll(PageRequest.of(0, 10)) } },
            { verify { mapper.toPrestamoResponseList(prestamos) } }
        )
    }

    @Test
    fun getPrestamoByGuid() {
        val guid = "test-guid"

        every { repository.findByGuid(guid) } returns prestamo
        every { mapper.toPrestamoResponseAdmin(prestamo) } returns adminResponse

        val result = service.getPrestamoByGuid(guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(adminResponse, result.value) },
            { verify { repository.findByGuid(guid) } },
            { verify { mapper.toPrestamoResponseAdmin(prestamo) } }
        )
    }

    @Test
    fun `getPrestamoByGuid returns Err when not found`() {
        val guid = "not-found-guid"
        every { repository.findByGuid(guid) } returns null

        val result = service.getPrestamoByGuid(guid)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is PrestamoError.PrestamoNotFound) },
            { verify { repository.findByGuid(guid) } },
            { verify(exactly = 0) { mapper.toPrestamoResponse(any()) } }
        )
    }

    @Test
    fun createPrestamo() {
        mockkStatic(SecurityContextHolder::class)

        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { webService.createAndSendNotification(any(), any()) } just Runs

        every { userRepository.findByEmail(user.email) } returns user
        every { dispositivoRepository.findByEstadoDispositivo(EstadoDispositivo.DISPONIBLE) } returns listOf(dispositivo)
        every { mapper.toPrestamoFromCreate(user, any()) } returns prestamo
        every { repository.save(prestamo) } returns prestamo
        every { dispositivoRepository.save(dispositivo) } returns dispositivo
        every { storage.generateAndSavePdf(prestamo.guid) } just Runs
        every { storage.generatePdf(prestamo.guid) } returns ByteArray(0)
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { emailService.sendHtmlEmailPrestamoCreado(any(), any(), any(), any(), any(), any(), any()) } just Runs
        every { mapper.toPrestamoResponse(prestamo) } returns response

        val result = service.createPrestamo()

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { userRepository.findByEmail(user.email) } },
            { verify { dispositivoRepository.findByEstadoDispositivo(EstadoDispositivo.DISPONIBLE) } },
            { verify { mapper.toPrestamoFromCreate(user, dispositivo) } },
            { verify { repository.save(prestamo) } },
            { verify { dispositivoRepository.save(dispositivo) } },
            { verify { storage.generateAndSavePdf(prestamo.guid) } },
            { verify { emailService.sendHtmlEmailPrestamoCreado(any(), any(), any(), any(), any(), any(), any()) } },
            { verify { mapper.toPrestamoResponse(prestamo) } },
        )
    }

    @Test
    fun `createPrestamo returns Err when user no existe`() {
        mockkStatic(SecurityContextHolder::class)

        val email = "notfound@test.com"
        val auth = mockk<Authentication>()
        every { auth.name } returns email
        every { SecurityContextHolder.getContext().authentication } returns auth

        every { userRepository.findByEmail(email) } returns null

        val result = service.createPrestamo()

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is PrestamoError.UserNotFound) },
            { verify { userRepository.findByEmail(email) } }
        )
    }

    @Test
    fun `createPrestamo returns Err when no dispositivos disponibles`() {
        mockkStatic(SecurityContextHolder::class)

        val email = user.email
        val auth = mockk<Authentication>()
        every { auth.name } returns email
        every { SecurityContextHolder.getContext().authentication } returns auth

        every { userRepository.findByEmail(email) } returns user
        every { dispositivoRepository.findByEstadoDispositivo(EstadoDispositivo.DISPONIBLE) } returns emptyList()

        val result = service.createPrestamo()

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is PrestamoError.DispositivoNotFound) },
            { verify { userRepository.findByEmail(email) } },
            { verify { dispositivoRepository.findByEstadoDispositivo(EstadoDispositivo.DISPONIBLE) } }
        )
    }

    @Test
    fun updatePrestamo() {
        mockkStatic(SecurityContextHolder::class)

        every { repository.findByGuid(prestamo.guid) } returns prestamo
        every { updateRequest.validate() } returns Ok(updateRequest)
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { repository.save(any()) } returns prestamo
        every { mapper.toPrestamoResponse(any()) } returns response
        every { updateRequest.estadoPrestamo } returns "vencido"

        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { webService.createAndSendNotification(any(), any()) } just Runs

        val result = service.updatePrestamo(prestamo.guid, updateRequest)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { repository.findByGuid(prestamo.guid) } },
            { verify { updateRequest.validate() } },
            { verify { repository.save(any()) } },
            { verify { mapper.toPrestamoResponse(any()) } },
        )
    }

    @Test
    fun `updatePrestamo returns Err when prestamo invalida`() {
        val request = PrestamoUpdateRequest(estadoPrestamo = "")

        mockkStatic("org.example.prestamoordenadores.utils.validators.PrestamoValidatorKt")
        every { request.validate() } returns Err(PrestamoError.PrestamoValidationError("Invalid"))

        val result = service.updatePrestamo(prestamo.guid, request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is PrestamoError.PrestamoValidationError) },
            { verify { request.validate() } }
        )
    }

    @Test
    fun `updatePrestamo returns Err when prestamo no existe`() {
        every { repository.findByGuid("prestamo-guid") } returns null
        every { updateRequest.estadoPrestamo } returns "VENCIDO"

        val result = service.updatePrestamo("prestamo-guid", updateRequest)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is PrestamoError.PrestamoNotFound) }
        )
    }

    @Test
    fun deletePrestamoByGuid() {
        mockkStatic(SecurityContextHolder::class)

        every { repository.findByGuid("9BR5JE350LA") } returns prestamo
        every { repository.save(prestamo) } returns prestamo
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { mapper.toPrestamoResponseAdmin(any()) } returns adminResponse

        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { webService.createAndSendNotification(any(), any()) } just Runs
        every { userRepository.findByEmail(user.email) } returns user

        val result = service.deletePrestamoByGuid("9BR5JE350LA")

        assertAll(
            { assertTrue(result.isOk) },
            { verify { repository.save(prestamo) } },
            { verify { repository.findByGuid("9BR5JE350LA") } },
            { verify { mapper.toPrestamoResponseAdmin(any()) } },
        )
    }

    @Test
    fun `deletePrestamoByGuid returns Err when prestamo no existe`() {
        mockkStatic(SecurityContextHolder::class)
        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { userRepository.findByEmail(user.email) } returns user

        every { repository.findByGuid("prestamo-guid") } returns null

        val result = service.deletePrestamoByGuid("prestamo-guid")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is PrestamoError.PrestamoNotFound) }
        )
    }

    @Test
    fun getByFechaPrestamo() {
        val prestamos = listOf(prestamo)
        every { repository.findByFechaPrestamo(prestamo.fechaPrestamo) } returns prestamos
        every { mapper.toPrestamoResponseList(prestamos) } returns listOf(response)

        val result = service.getByFechaPrestamo(prestamo.fechaPrestamo)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(listOf(response), result.value) },
            { verify { repository.findByFechaPrestamo(prestamo.fechaPrestamo) } },
            { verify { mapper.toPrestamoResponseList(prestamos) } }
        )
    }

    @Test
    fun getByFechaDevolucion() {
        val prestamos = listOf(prestamo)
        every { repository.findByFechaDevolucion(prestamo.fechaDevolucion) } returns prestamos
        every { mapper.toPrestamoResponseList(prestamos) } returns listOf(response)

        val result = service.getByFechaDevolucion(prestamo.fechaDevolucion)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(listOf(response), result.value) },
            { verify { repository.findByFechaDevolucion(prestamo.fechaDevolucion) } },
            { verify { mapper.toPrestamoResponseList(prestamos) } }
        )
    }

    @Test
    fun getPrestamoByUserGuid() {
        val prestamos = listOf(prestamo)
        val responseList = listOf(response)

        every { userRepository.findByGuid(user.guid) } returns user
        every { repository.findByUserGuid(user.guid) } returns prestamos
        every { mapper.toPrestamoResponseList(prestamos) } returns responseList

        val result = service.getPrestamoByUserGuid(user.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(responseList, result.value) },
            { verify { userRepository.findByGuid(user.guid) } },
            { verify { repository.findByUserGuid(user.guid) } },
            { verify { mapper.toPrestamoResponseList(prestamos) } }
        )
    }

    @Test
    fun `getPrestamoByUserGuid returns Err when user no existe`() {
        every { userRepository.findByGuid("user-guid") } returns null

        val result = service.getPrestamoByUserGuid("user-guid")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is PrestamoError.UserNotFound) },
            { verify { userRepository.findByGuid("user-guid") } }
        )
    }
}