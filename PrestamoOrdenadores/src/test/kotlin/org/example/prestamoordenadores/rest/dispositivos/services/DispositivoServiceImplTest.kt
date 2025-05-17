package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Ok
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.example.prestamoordenadores.config.websockets.WebSocketService
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.rest.dispositivos.mappers.DispositivoMapper
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.validators.validate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class DispositivoServiceImplTest {
    @MockK
    lateinit var repository: DispositivoRepository

    @MockK
    lateinit var mapper: DispositivoMapper

    @MockK
    lateinit var incidenciasRepository: IncidenciaRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var webService: WebSocketService

    @MockK
    lateinit var mockAuthentication: Authentication
    @MockK
    lateinit var mockSecurityContext: SecurityContext

    lateinit var service: DispositivoServiceImpl

    var dispositivo : Dispositivo = Dispositivo()
    var incidencia : Incidencia = Incidencia()
    var user : User = User()

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
            rol = Role.ADMIN,
            isActivo = true,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            isDeleted = false,
            isOlvidado = false
        )

        incidencia = Incidencia(
            id = 1,
            guid = "INC000000",
            asunto = "asunto",
            descripcion = "descripcion",
            estadoIncidencia = EstadoIncidencia.PENDIENTE,
            user = user,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            isDeleted = false
        )

        dispositivo = Dispositivo(
            id = 1,
            guid = "guidTestD01",
            numeroSerie = "2ZY098ABCD",
            componentes = "ratón",
            estadoDispositivo = EstadoDispositivo.DISPONIBLE,
            incidencia = incidencia,
            isDeleted = false,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        service = DispositivoServiceImpl(repository, mapper, incidenciasRepository, userRepository, webService)
    }

    @Test
    fun getAllDispositivos() {
        val dispositivos = listOf(dispositivo)

        val dispositivoAdmin = DispositivoResponseAdmin(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            estado = dispositivo.estadoDispositivo.toString(),
            incidencia = dispositivo.incidencia,
            isDeleted = dispositivo.isDeleted
        )

        val responses = listOf(dispositivoAdmin)
        every { repository.findAll(any<PageRequest>()) } returns PageImpl(dispositivos)
        every { mapper.toDispositivoResponseListAdmin(dispositivos) } returns responses

        val result = service.getAllDispositivos(0, 10)

        assertTrue(result.isOk)
        val paged = result.value
        assertEquals(1, paged.totalElements)
        assertEquals(responses, paged.content)
    }

    @Test
    fun getDispositivoByGuid() {
        val response = DispositivoResponseAdmin(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            estado = dispositivo.estadoDispositivo.toString(),
            incidencia = dispositivo.incidencia,
            isDeleted = dispositivo.isDeleted
        )

        every { repository.findDispositivoByGuid("guidTestD01") } returns dispositivo
        every { mapper.toDispositivoResponseAdmin(dispositivo) } returns response

        val result = service.getDispositivoByGuid("guidTestD01")

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { repository.findDispositivoByGuid("guidTestD01") } },
            { verify { mapper.toDispositivoResponseAdmin(dispositivo) } }
        )
    }

    @Test
    fun `getDispositivoByGuid returns Err when dispositivo no existe`() {
        val guid = "guid123"

        every { repository.findDispositivoByGuid(guid) } returns null

        val result = service.getDispositivoByGuid(guid)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is DispositivoError.DispositivoNotFound) },
            { assertEquals("Dispositivo con GUID: $guid no encontrado", result.error.message) },
            { verify { repository.findDispositivoByGuid(guid) } }
        )
    }

    @Test
    fun createDispositivo() {
        val dispositivoRequest = DispositivoCreateRequest(
            numeroSerie = "1AB123ABCD",
            componentes = "cargador"
        )

        every { userRepository.findByEmail("test@example.com") } returns user

        val deviceEntity = mockk<Dispositivo>()
        every { deviceEntity.numeroSerie } returns "1AB123ABCD"
        every { deviceEntity.guid } returns "DEVICE-GUID-123"

        val deviceResponse = mockk<DispositivoResponse>()

        every { mapper.toDispositivoFromCreate(dispositivoRequest) } returns deviceEntity
        every { repository.save(deviceEntity) } returns deviceEntity
        every { mapper.toDispositivoResponse(deviceEntity) } returns deviceResponse

        every { mockAuthentication.isAuthenticated } returns true
        every { mockAuthentication.principal } returns "test@example.com"
        every { mockSecurityContext.authentication } returns mockAuthentication
        SecurityContextHolder.setContext(mockSecurityContext)
        every { webService.createAndSendNotification(any(), any()) } just Runs
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(user)

        val result = service.createDispositivo(dispositivoRequest)

        assertTrue(result.isOk)
        assertEquals(deviceResponse, result.value)

        verify {
            userRepository.findByEmail("test@example.com")
            mapper.toDispositivoFromCreate(dispositivoRequest)
            repository.save(deviceEntity)
            mapper.toDispositivoResponse(deviceEntity)
        }
    }

    @Test
    fun `createDispositivo returns Err when user no existe`() {
        val userEmail = "noexiste@example.com"
        val dispositivoCreateRequest = DispositivoCreateRequest(
            numeroSerie = "1AB123ABCD",
            componentes = "cargador"
        )

        val auth = mockk<Authentication>()
        every { auth.isAuthenticated } returns true
        every { auth.principal } returns userEmail

        val context = mockk<SecurityContext>()
        every { context.authentication } returns auth
        SecurityContextHolder.setContext(context)

        every { userRepository.findByEmail(userEmail) } returns null

        val result = service.createDispositivo(dispositivoCreateRequest)

        assertTrue(result.isErr)
        val error = result.error
        assertTrue(error is DispositivoError.UserNotFound)
        assertEquals("Usuario $userEmail no encontrado.", (error as DispositivoError.UserNotFound).message)

        verify(exactly = 1) { userRepository.findByEmail(userEmail) }
    }

    @Test
    fun updateDispositivo() {
        val guid = "guidTestD55"
        val request = DispositivoUpdateRequest(
            incidenciaGuid = null,
            componentes = "RAM 16GB",
            estadoDispositivo = "DISPONIBLE",
            isDeleted = true
        )

        val existingDispositivo = mockk<Dispositivo>(relaxed = true)
        val expectedResponse = mockk<DispositivoResponseAdmin>()

        every { repository.findDispositivoByGuid(guid) } returns existingDispositivo
        every { repository.save(existingDispositivo) } returns existingDispositivo
        every { mapper.toDispositivoResponseAdmin(existingDispositivo) } returns expectedResponse

        val result = service.updateDispositivo(guid, request)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findDispositivoByGuid(guid) } },
            { verify { repository.save(existingDispositivo) } },
            { verify { mapper.toDispositivoResponseAdmin(existingDispositivo) } }
        )
    }

    @Test
    fun `updateDispositivo returns Err when dispositivo no existe`() {
        val guid = "device-guid"
        val request = DispositivoUpdateRequest(
            incidenciaGuid = null,
            componentes = null,
            estadoDispositivo = null,
            isDeleted = null
        )

        every { repository.findDispositivoByGuid(guid) } returns null

        val result = service.updateDispositivo(guid, request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is DispositivoError.DispositivoNotFound) },
            { assertEquals("Dispositivo con GUID: $guid no encontrado", result.error.message) },
            { verify { repository.findDispositivoByGuid(guid) } }
        )
    }

    @Test
    fun `updateDispositivo returns Err when incidencia no existe`() {
        val guid = "device-guid"
        val request = DispositivoUpdateRequest(
            incidenciaGuid = "inc-guid",
            componentes = null,
            estadoDispositivo = null,
            isDeleted = null
        )

        val existingDispositivo = mockk<Dispositivo>(relaxed = true)

        every { repository.findDispositivoByGuid(guid) } returns existingDispositivo
        every { incidenciasRepository.findIncidenciaByGuid("inc-guid") } returns null

        val result = service.updateDispositivo(guid, request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is DispositivoError.IncidenciaNotFound) },
            { assertEquals("Incidencia con GUID: inc-guid no encontrada", (result.error as DispositivoError.IncidenciaNotFound).message) },
            { verify { repository.findDispositivoByGuid(guid) } },
            { verify { incidenciasRepository.findIncidenciaByGuid("inc-guid") } }
        )
    }

    @Test
    fun deleteDispositivoByGuid() {
        val guid = "device-guid"
        val dispositivo = mockk<Dispositivo>(relaxed = true)
        val expectedResponse = mockk<DispositivoResponseAdmin>()

        every { repository.findDispositivoByGuid(guid) } returns dispositivo
        every { repository.save(dispositivo) } returns dispositivo
        every { mapper.toDispositivoResponseAdmin(dispositivo) } returns expectedResponse

        val result = service.deleteDispositivoByGuid(guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findDispositivoByGuid(guid) } },
            { verify { repository.save(dispositivo) } },
            { verify { mapper.toDispositivoResponseAdmin(dispositivo) } }
        )
    }

    @Test
    fun `deleteDispositivoByGuid returns Err when dispositivo no existe`() {
        val guid = "nonexistent-guid"

        every { repository.findDispositivoByGuid(guid) } returns null

        val result = service.deleteDispositivoByGuid(guid)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is DispositivoError.DispositivoNotFound) },
            { assertEquals("Dispositivo con GUID: $guid no encontrado", result.error.message) },
            { verify { repository.findDispositivoByGuid(guid) } }
        )
    }

    @Test
    fun getDispositivoByNumeroSerie() {
        val numeroSerie = "ABC123"
        val dispositivo = mockk<Dispositivo>()
        val expectedResponse = mockk<DispositivoResponseAdmin>()

        every { repository.findByNumeroSerie(numeroSerie) } returns dispositivo
        every { mapper.toDispositivoResponseAdmin(dispositivo) } returns expectedResponse

        val result = service.getDispositivoByNumeroSerie(numeroSerie)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findByNumeroSerie(numeroSerie) } },
            { verify { mapper.toDispositivoResponseAdmin(dispositivo) } }
        )
    }

    @Test
    fun `getDispositivoByNumeroSerie returns Err when dispositivo no existe`() {
        val numeroSerie = "NO_EXISTE"

        every { repository.findByNumeroSerie(numeroSerie) } returns null

        val result = service.getDispositivoByNumeroSerie(numeroSerie)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is DispositivoError.DispositivoNotFound) },
            { assertEquals("Dispositivo con numero de serie: $numeroSerie no encontrado", result.error.message) },
            { verify { repository.findByNumeroSerie(numeroSerie) } }
        )
    }

    @Test
    fun getDispositivoByEstado() {
        val estado = "disponible"
        val estadoEnum = EstadoDispositivo.DISPONIBLE
        val dispositivos = listOf(mockk<Dispositivo>())
        val expectedResponse = listOf(mockk<DispositivoResponseAdmin>())

        every { repository.findByEstadoDispositivo(estadoEnum) } returns dispositivos
        every { mapper.toDispositivoResponseListAdmin(dispositivos) } returns expectedResponse

        val result = service.getDispositivoByEstado(estado)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findByEstadoDispositivo(estadoEnum) } },
            { verify { mapper.toDispositivoResponseListAdmin(dispositivos) } }
        )
    }

    @Test
    fun `getDispositivoByEstado returns Err when estado invalido`() {
        val estado = "estado_inexistente"

        val result = service.getDispositivoByEstado(estado)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is DispositivoError.DispositivoNotFound) },
            { assertEquals("Dispositivo con estado '$estado' no encontrado", result.error.message) }
        )
    }

    @Test
    fun getStock() {
        val dispositivos = listOf(
            mockk<Dispositivo>(),
            mockk<Dispositivo>(),
            mockk<Dispositivo>()
        )

        every { repository.findAll() } returns dispositivos

        val result = service.getStock()

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(3, result.value) },
            { verify { repository.findAll() } }
        )
    }
}