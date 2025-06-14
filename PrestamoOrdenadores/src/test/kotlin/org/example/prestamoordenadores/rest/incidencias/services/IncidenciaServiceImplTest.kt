package org.example.prestamoordenadores.rest.incidencias.services

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
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponseAdmin
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.example.prestamoordenadores.rest.incidencias.mappers.IncidenciaMapper
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
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
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class IncidenciaServiceImplTest {

    @MockK
    lateinit var repository: IncidenciaRepository

    @MockK
    lateinit var mapper: IncidenciaMapper

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var webService: WebSocketService

    @MockK
    lateinit var createRequest: IncidenciaCreateRequest

    @MockK
    lateinit var updateRequest: IncidenciaUpdateRequest

    lateinit var service: IncidenciaServiceImpl

    var user = User()
    var incidencia = Incidencia()
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
    var incidenciaResponse = IncidenciaResponse(
        guid = incidencia.guid,
        asunto = incidencia.asunto,
        descripcion = incidencia.descripcion,
        estadoIncidencia = incidencia.estadoIncidencia.toString(),
        user = userResponse,
        createdDate = incidencia.createdDate.toString()
    )
    var incidenciaResponseAdmin = IncidenciaResponseAdmin(
        guid = incidencia.guid,
        asunto = incidencia.asunto,
        descripcion = incidencia.descripcion,
        estadoIncidencia = incidencia.estadoIncidencia.toString(),
        user = userResponse,
        createdDate = incidencia.createdDate.toString(),
        updatedDate = incidencia.updatedDate.toString(),
        isDeleted = false
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

        service = IncidenciaServiceImpl(
            repository,
            mapper,
            userRepository,
            webService
        )
    }

    @Test
    fun getAllIncidencias() {
        val page = PageImpl(listOf(incidencia))
        every { repository.findAll(any<PageRequest>()) } returns page
        every { mapper.toIncidenciaResponseList(any()) } returns listOf(incidenciaResponse)

        val result = service.getAllIncidencias(0, 10)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(listOf(incidenciaResponse), result.value.content) },
            { verify { repository.findAll(any<PageRequest>()) } },
            { verify { mapper.toIncidenciaResponseList(any()) } }
        )
    }

    @Test
    fun getIncidenciaByGuid() {
        every { repository.findIncidenciaByGuid("INC0001") } returns incidencia
        every { mapper.toIncidenciaResponse(any()) } returns incidenciaResponse

        val result = service.getIncidenciaByGuid("INC0001")

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(incidenciaResponse, result.value) },
            { verify { repository.findIncidenciaByGuid("INC0001") } },
            { verify { mapper.toIncidenciaResponse(any()) } }
        )
    }

    @Test
    fun `getIncidenciaByGuid returns Err when incidencia no existe`() {
        every { repository.findIncidenciaByGuid("INC0001") } returns null

        val result = service.getIncidenciaByGuid("INC0001")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is IncidenciaError.IncidenciaNotFound) },
            { assertEquals("Incidencia no encontrada", result.error.message) },
            { verify { repository.findIncidenciaByGuid("INC0001") } }
        )
    }

    @Test
    fun getIncidenciaByGuidAdmin() {
        every { repository.findIncidenciaByGuid("INC000000") } returns incidencia
        every { mapper.toIncidenciaResponseAdmin(incidencia) } returns mockk()

        val result = service.getIncidenciaByGuidAdmin("INC000000")

        assertTrue(result.isOk)
        verify { repository.findIncidenciaByGuid("INC000000") }
        verify { mapper.toIncidenciaResponseAdmin(incidencia) }
    }

    @Test
    fun `getIncidenciaByGuidAdmin returns Err when incidencia no existe`() {
        every { repository.findIncidenciaByGuid("INC000001") } returns null

        val result = service.getIncidenciaByGuidAdmin("INC000001")

        assertTrue(result.isErr)
        assertTrue(result.error is IncidenciaError.IncidenciaNotFound)
    }

    @Test
    fun createIncidencia() {
        every { createRequest.validate() } returns Ok(createRequest)
        every { createRequest.asunto } returns "asunto"
        every { createRequest.descripcion } returns "descripcion"
        every { userRepository.findByEmail(user.email) } returns user
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { mapper.toIncidenciaFromCreate(createRequest, user) } returns incidencia
        every { repository.save(incidencia) } returns incidencia
        every { mapper.toIncidenciaResponse(incidencia) } returns incidenciaResponse

        mockkStatic(SecurityContextHolder::class)
        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { webService.createAndSendNotification(any(), any()) } just Runs

        val result = service.createIncidencia(createRequest)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(incidenciaResponse, result.value) },
            { verify { createRequest.validate() } },
            { verify { mapper.toIncidenciaFromCreate(createRequest, user) } },
            { verify { repository.save(incidencia) } },
            { verify { mapper.toIncidenciaResponse(incidencia) } },
        )
    }

    @Test
    fun `createIncidencia returns Err when request es invalido`() {
        every { createRequest.validate() } returns Err(IncidenciaError.IncidenciaValidationError("inválido"))
        every { createRequest.asunto } returns ""
        every { createRequest.descripcion } returns ""

        val result = service.createIncidencia(createRequest)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is IncidenciaError.IncidenciaValidationError) },
            { assertEquals("Incidencia inválida", result.error.message) },
            { verify { createRequest.validate() } }
        )
    }

    @Test
    fun updateIncidencia() {
        every { repository.findIncidenciaByGuid("INC0001") } returns incidencia
        every { updateRequest.validate() } returns Ok(updateRequest)
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { repository.save(any()) } returns incidencia
        every { mapper.toIncidenciaResponse(any()) } returns incidenciaResponse
        every { updateRequest.estadoIncidencia } returns "pendiente"

        mockkStatic(SecurityContextHolder::class)
        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { userRepository.findByEmail(user.email) } returns user
        every { webService.createAndSendNotification(any(), any()) } just Runs

        val result = service.updateIncidencia("INC0001", updateRequest)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(incidenciaResponse, result.value) },
            { verify { repository.findIncidenciaByGuid("INC0001") } },
            { verify { updateRequest.validate() } },
            { verify { repository.save(any()) } },
            { verify { mapper.toIncidenciaResponse(any()) } },
        )
    }

    @Test
    fun `updateIncidencia returns Err when incidencia no existe`() {
        every { repository.findIncidenciaByGuid("INC0001") } returns null
        mockkStatic(SecurityContextHolder::class)
        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { userRepository.findByEmail(user.email) } returns user

        val result = service.updateIncidencia("INC0001", updateRequest)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is IncidenciaError.IncidenciaNotFound) },
            { assertEquals("Incidencia no encontrada", result.error.message) },
            { verify { repository.findIncidenciaByGuid("INC0001") } }
        )
    }

    @Test
    fun deleteIncidenciaByGuid() {
        every { repository.findIncidenciaByGuid("INC0001") } returns incidencia
        every { repository.save(incidencia) } returns incidencia
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin.loantech@gmail.com", rol = Role.ADMIN))
        every { mapper.toIncidenciaResponseAdmin(any()) } returns incidenciaResponseAdmin
        mockkStatic(SecurityContextHolder::class)
        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { userRepository.findByEmail(user.email) } returns user
        every { webService.createAndSendNotification(any(), any()) } just Runs

        val result = service.deleteIncidenciaByGuid("INC0001")

        assertAll(
            { assertTrue(result.isOk) },
            { verify { repository.findIncidenciaByGuid("INC0001") } },
            { verify { repository.save(incidencia) } },
            { verify { mapper.toIncidenciaResponseAdmin(any()) } },
        )
    }

    @Test
    fun `deleteIncidenciaByGuid returns Err when incidencia no existe`() {
        every { repository.findIncidenciaByGuid("INC0001") } returns null
        mockkStatic(SecurityContextHolder::class)
        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth
        every { userRepository.findByEmail(user.email) } returns user

        val result = service.deleteIncidenciaByGuid("INC0001")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is IncidenciaError.IncidenciaNotFound) },
            { assertEquals("Incidencia no encontrada", result.error.message) },
            { verify { repository.findIncidenciaByGuid("INC0001") } }
        )
    }

    @Test
    fun getIncidenciaByEstado() {
        every { repository.findIncidenciasByEstadoIncidencia(EstadoIncidencia.PENDIENTE) } returns listOf(incidencia)
        every { mapper.toIncidenciaResponseList(any()) } returns listOf(incidenciaResponse)

        val result = service.getIncidenciaByEstado("pendiente")

        assertTrue(result.isOk)
        assertEquals(1, result.value.size)
    }

    @Test
    fun `getIncidenciaByEstado returns Err when estado invalido`() {
        val result = service.getIncidenciaByEstado("inexistente")

        assertTrue(result.isErr)
        assertTrue(result.error is IncidenciaError.IncidenciaNotFound)
    }

    @Test
    fun getIncidenciasByUserGuid() {
        every { userRepository.findByGuid(user.guid) } returns user
        every { repository.findIncidenciasByUserGuid(user.guid) } returns listOf(incidencia)
        every { mapper.toIncidenciaResponseList(any()) } returns listOf(incidenciaResponse)

        val result = service.getIncidenciasByUserGuid(user.guid)

        assertTrue(result.isOk)
        assertEquals(1, result.value.size)
    }

    @Test
    fun `getIncidenciasByUserGuid returns Err when user no existe`() {
        every { userRepository.findByGuid("user-fake") } returns null

        val result = service.getIncidenciasByUserGuid("user-fake")

        assertTrue(result.isErr)
        assertTrue(result.error is IncidenciaError.UserNotFound)
    }
}