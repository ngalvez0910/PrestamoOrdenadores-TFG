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
    lateinit var webSocketHandler: WebSocketHandler

    @MockK
    lateinit var objectMapper: ObjectMapper

    @MockK
    lateinit var createRequest: SancionRequest

    @MockK
    lateinit var updateRequest: SancionUpdateRequest

    lateinit var service: SancionServiceImpl

    var user = User()
    var sancion = Sancion()
    var userResponse = UserResponse(
        guid = user.guid,
        email = user.email,
        nombre = user.nombre,
        apellidos = user.apellidos,
        curso = user.curso!!,
        tutor = user.tutor!!
    )
    var response = SancionResponse(
        guid = sancion.guid,
        user = userResponse,
        tipoSancion = sancion.tipoSancion.toString(),
        fechaSancion = sancion.fechaSancion.toString()
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
            objectMapper,
            webSocketHandler
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
    fun createSancion() {
        val createRequest = SancionRequest(
            userGuid = sancion.user.guid,
            tipoSancion = sancion.tipoSancion.toString()
        )

        mockkStatic("org.example.prestamoordenadores.utils.validators.SancionValidatorKt")
        every { createRequest.validate() } returns Ok(createRequest)

        every { userRepository.findByGuid(user.guid) } returns user
        every { mapper.toSancionFromRequest(createRequest, userRepository) } returns sancion
        every { repository.save(sancion) } returns sancion
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin@loantech.com", rol = Role.ADMIN))
        every { userRepository.findByEmail(user.email) } returns user
        every { mapper.toSancionResponse(sancion) } returns response
        every { objectMapper.writeValueAsString(any()) } returns "{}"
        every { webSocketHandler.sendMessageToUser(user.email, any()) } just Runs

        mockkStatic(SecurityContextHolder::class)
        val auth = mockk<Authentication>()
        every { auth.name } returns user.email
        every { SecurityContextHolder.getContext().authentication } returns auth

        val result = service.createSancion(createRequest)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { createRequest.validate() } },
            { verify { mapper.toSancionFromRequest(createRequest, userRepository) } },
            { verify { repository.save(sancion) } },
            { verify { mapper.toSancionResponse(sancion) } },
            { verify { objectMapper.writeValueAsString(any()) } },
            { verify { webSocketHandler.sendMessageToUser(user.email, any()) } },
        )
    }

    @Test
    fun `createSancion returns error if validation fails`() {
        every { createRequest.validate() } returns Err(SancionError.SancionValidationError("Sanción inválida"))
        every { createRequest.userGuid } returns ""

        val result = service.createSancion(createRequest)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.SancionValidationError) },
            { verify { createRequest.validate() } }
        )
    }

    @Test
    fun `createSancion returns Err when user no existe`() {
        every { userRepository.findByGuid("user-guid") } returns null
        every { createRequest.userGuid } returns "user-guid"
        every { createRequest.tipoSancion } returns "BLOQUEO_TEMPORAL"

        val result = service.createSancion(createRequest)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is SancionError.UserNotFound) },
            { verify { userRepository.findByGuid("user-guid") } }
        )
    }

    @Test
    fun updateSancion() {
        every { repository.findByGuid(sancion.guid) } returns sancion
        every { updateRequest.validate() } returns Ok(updateRequest)
        every { userRepository.findUsersByRol(Role.ADMIN) } returns listOf(User(email = "admin@loantech.com", rol = Role.ADMIN))
        every { repository.save(any()) } returns sancion
        every { mapper.toSancionResponse(any()) } returns response
        every { objectMapper.writeValueAsString(any()) } returns "{}"
        every { webSocketHandler.sendMessageToUser(any(), any()) } just Runs
        every { updateRequest.tipoSancion } returns "bloqueo_temporal"

        val result = service.updateSancion(sancion.guid, updateRequest)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(response, result.value) },
            { verify { repository.findByGuid(sancion.guid) } },
            { verify { updateRequest.validate() } },
            { verify { repository.save(any()) } },
            { verify { mapper.toSancionResponse(any()) } },
            { verify { objectMapper.writeValueAsString(any()) } },
            { verify { webSocketHandler.sendMessageToUser(any(), any()) } }
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
        every { objectMapper.writeValueAsString(any()) } returns "{}"
        every { webSocketHandler.sendMessageToUser(any(), any()) } just Runs

        val result = service.deleteSancionByGuid("SANC0001")

        assertAll(
            { assertTrue(result.isOk) },
            { verify { repository.findByGuid("SANC0001") } },
            { verify { repository.delete(sancion) } },
            { verify { mapper.toSancionResponse(any()) } },
            { verify { objectMapper.writeValueAsString(any()) } },
            { verify { webSocketHandler.sendMessageToUser(any(), any()) } }
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
}