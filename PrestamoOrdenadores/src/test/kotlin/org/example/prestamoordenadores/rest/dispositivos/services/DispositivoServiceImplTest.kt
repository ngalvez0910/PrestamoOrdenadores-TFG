package org.example.prestamoordenadores.rest.dispositivos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
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
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.services.UserServiceImpl
import org.example.prestamoordenadores.utils.validators.validate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
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
    lateinit var createRequest: DispositivoCreateRequest

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
            rol = Role.ALUMNO,
            isActivo = true,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        incidencia = Incidencia(
            id = 1,
            guid = "INC000000",
            asunto = "asunto",
            descripcion = "descripcion",
            estadoIncidencia = EstadoIncidencia.PENDIENTE,
            user = user,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        dispositivo = Dispositivo(
            id = 1,
            guid = "guidTestD01",
            numeroSerie = "2ZY098ABCD",
            componentes = "ratón",
            estadoDispositivo = EstadoDispositivo.DISPONIBLE,
            incidencia = incidencia,
            isActivo = true,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        service = DispositivoServiceImpl(repository, mapper, incidenciasRepository)
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
            isActivo = dispositivo.isActivo
        )

        val responses = listOf(dispositivoAdmin)
        every { repository.findAll(any<PageRequest>()) } returns PageImpl(dispositivos)
        every { mapper.toDispositivoResponseListAdmin(dispositivos) } returns responses

        val result = service.getAllDispositivos(0, 10)

        assertTrue(result.isOk)
        assertEquals(responses, result.value)
    }

    @Test
    fun getDispositivoByGuid() {
        val response = DispositivoResponseAdmin(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            estado = dispositivo.estadoDispositivo.toString(),
            incidencia = dispositivo.incidencia,
            isActivo = dispositivo.isActivo
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
        val response = DispositivoResponse(
            guid = "guidTestD02",
            numeroSerie = "4JT8695OPQ",
            componentes = "cargador"
        )

        every { createRequest.validate() } returns Ok(createRequest)
        every { createRequest.componentes } returns "cargador"
        every { mapper.toDispositivoFromCreate(createRequest) } returns dispositivo
        every { repository.save(dispositivo) } returns dispositivo
        every { mapper.toDispositivoResponse(dispositivo) } returns response

        val result = service.createDispositivo(createRequest)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { createRequest.validate() } },
            { verify { mapper.toDispositivoFromCreate(createRequest) } },
            { verify { repository.save(dispositivo) } },
            { verify { mapper.toDispositivoResponse(dispositivo) } }
        )
    }

    @Test
    fun `createDispositivo returns Err when request es invalido`() {
        val request = mockk<DispositivoCreateRequest>(relaxed = true)
        val validationError = DispositivoError.DispositivoValidationError("Dispositivo inválido")

        every { request.validate() } returns Err(validationError)

        val result = service.createDispositivo(request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is DispositivoError.DispositivoValidationError) },
            { assertEquals(validationError, result.error) },
            { verify { request.validate() } }
        )
    }

    @Test
    fun updateDispositivo() {
        val guid = "guidTestD55"
        val request = DispositivoUpdateRequest(
            incidenciaGuid = null,
            componentes = "RAM 16GB",
            estado = "DISPONIBLE",
            isActivo = true
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
            estado = null,
            isActivo = null
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
            estado = null,
            isActivo = null
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
        val expectedResponse = mockk<DispositivoResponse>()

        every { repository.findDispositivoByGuid(guid) } returns dispositivo
        every { repository.save(dispositivo) } returns dispositivo
        every { mapper.toDispositivoResponse(dispositivo) } returns expectedResponse

        val result = service.deleteDispositivoByGuid(guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findDispositivoByGuid(guid) } },
            { verify { repository.save(dispositivo) } },
            { verify { mapper.toDispositivoResponse(dispositivo) } }
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