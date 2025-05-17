package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.dto.UserUpdateRequest
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.validators.validate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class UserServiceImplTest {
    @MockK
    lateinit var repository: UserRepository

    @MockK
    lateinit var prestamoRepository: PrestamoRepository

    @MockK
    lateinit var incidenciaRepository: IncidenciaRepository

    @MockK
    lateinit var dispositivoRepository: DispositivoRepository

    @MockK
    lateinit var sancionRepository: SancionRepository

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @MockK
    lateinit var mapper: UserMapper

    @MockK
    lateinit var request: UserAvatarUpdateRequest

    @MockK
    lateinit var request2: UserPasswordResetRequest

    lateinit var service: UserServiceImpl

    var user : User = User()

    @BeforeEach
    fun setUp() {
        user = User(
            id = 1,
            guid = "guidTestU01",
            email = "email@loantech.com",
            numeroIdentificacion = "2023LT242",
            campoPassword = "Password123?",
            nombre = "nombre",
            apellidos = "apellidos",
            curso = "curso",
            tutor = "tutor",
            avatar = "avatar.png",
            rol = Role.ALUMNO,
            isActivo = true,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        service = UserServiceImpl(repository, mapper, incidenciaRepository, prestamoRepository, sancionRepository, dispositivoRepository, passwordEncoder)
    }

    @Test
    fun getAllUsers() {
        val page = 0
        val size = 1
        val userList = listOf(user
        )

        val responseList = listOf(
            UserResponseAdmin(
                user.numeroIdentificacion,
                user.guid,
                user.email,
                user.nombre,
                user.apellidos,
                user.curso!!,
                user.tutor!!,
                user.rol,
                user.isActivo,
                user.createdDate.toString(),
                user.updatedDate.toString(),
                user.lastLoginDate.toString(),
                user.lastPasswordResetDate.toString(),
                false,
                false
            )
        )

        val pageRequest = PageRequest.of(page, size)
        val pageImpl = PageImpl(userList, pageRequest, userList.size.toLong())

        every { repository.findAll(pageRequest) } returns pageImpl
        every { mapper.toUserResponseListAdmin(userList) } returns responseList

        val result = service.getAllUsers(page, size)

        assertTrue(result.isOk)
        val paged = result.value

        assertEquals(1, paged.content.size)
        assertEquals("guidTestU01", paged.content[0].guid)
        assertEquals(1, paged.totalElements)

        verify { repository.findAll(pageRequest) }
        verify { mapper.toUserResponseListAdmin(userList) }
    }

    @Test
    fun getUserByGuid() {
        val expectedResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )

        every { repository.findByGuid(user.guid) } returns user
        every { mapper.toUserResponse(user) } returns expectedResponse

        val result = service.getUserByGuid(user.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, (result.value)) },
            { verify { repository.findByGuid(user.guid) } },
            { verify { mapper.toUserResponse(user) } }
        )
    }

    @Test
    fun `getUserByGuid return Err when user no existe`() {
        every { repository.findByGuid("not-found-guid") } returns null

        val result = service.getUserByGuid("not-found-guid")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserNotFound) },
            { assertEquals("Usuario con GUID not-found-guid no encontrado", result.error.message) },
            { verify { repository.findByGuid("not-found-guid") } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun updateAvatar() {
        val expectedResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )

        every { request.validate() } returns Ok(request)
        every { request.avatar } returns "newAvatar.png"
        every { repository.findByGuid("guidTestU01") } returns user
        every { repository.save(any()) } returns user
        every { mapper.toUserResponse(user) } returns expectedResponse

        val result = service.updateAvatar("guidTestU01", request)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertEquals("newAvatar.png", user.avatar) },
            { verify { repository.findByGuid("guidTestU01") } },
            { verify { repository.save(user) } },
            { verify { mapper.toUserResponse(user) } }
        )
    }

    @Test
    fun `updateAvatar returns Err when request es invalido`() {
        every { request.validate() } returns Err(UserError.UserValidationError("Usuario inválido"))
        every { request.avatar } returns ""

        val result = service.updateAvatar("guidTestU01", request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserValidationError) },
            { assertEquals("Usuario inválido", result.error.message) },
            { verify(exactly = 0) { repository.findByGuid(any()) } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun `updateAvatar returns Err when user no existe`() {
        every { request.validate() } returns Ok(request)
        every { request.avatar } returns "newAvatar.png"
        every { repository.findByGuid("guid123") } returns null

        val result = service.updateAvatar("guid123", request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserNotFound) },
            { assertEquals("Usuario con GUID guid123 no encontrado", result.error.message) },
            { verify { repository.findByGuid("guid123") } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun deleteUserByGuid() {
        val user2 = User(
            id = 2,
            guid = "guidTestU02",
            email = "email2@loantech.com",
            numeroIdentificacion = "2023LT243",
            campoPassword = "Password123?",
            nombre = "nombre2",
            apellidos = "apellidos2",
            curso = "curso2",
            tutor = "tutor2",
            avatar = "avatar2.png",
            rol = Role.ALUMNO,
            isActivo = false,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        val expectedResponse = UserResponseAdmin(
            user2.numeroIdentificacion,
            user2.guid,
            user2.email,
            user2.nombre,
            user2.apellidos,
            user2.curso!!,
            user2.tutor!!,
            user2.rol,
            true,
            user2.createdDate.toString(),
            user2.updatedDate.toString(),
            user2.lastLoginDate.toString(),
            user2.lastPasswordResetDate.toString(),
            false,
            false
        )

        every { repository.findByGuid(user2.guid) } returns user2
        every { repository.save(any()) } returns user2
        every { mapper.toUserResponseAdmin(user2) } returns expectedResponse

        val result = service.deleteUserByGuid(user2.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertFalse(user2.isActivo) },
            { verify { repository.findByGuid(user2.guid) } },
            { verify { repository.save(user2) } },
            { verify { mapper.toUserResponseAdmin(user2) } }
        )
    }

    @Test
    fun `deleteUserByGuid returns Err when user no existe`() {
        every { repository.findByGuid("guidTestNE") } returns null

        val result = service.deleteUserByGuid("guidTestNE")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserNotFound) },
            { assertEquals("Usuario con GUID ${"guidTestNE"} no encontrado", (result.error as UserError.UserNotFound).message) },
            { verify { repository.findByGuid("guidTestNE") } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun resetPassword() {
        val expectedResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )

        every { request2.validate() } returns Ok(request2)
        every { request2.oldPassword } returns "Password123?"
        every { request2.newPassword } returns "NewPassword123?"
        every { request2.confirmPassword } returns "NewPassword123?"

        every { repository.findByGuid(user.guid) } returns user
        every { passwordEncoder.encode("NewPassword123?") } returns "NewPassword123?"
        every { repository.save(any()) } returns user
        every { mapper.toUserResponse(user) } returns expectedResponse

        val result = service.resetPassword(user.guid, request2)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertEquals("NewPassword123?", user.campoPassword) },
            { verify { request2.validate() } },
            { verify { repository.findByGuid(user.guid) } },
            { verify { repository.save(any()) } },
            { verify { mapper.toUserResponse(user) } },
            { verify { passwordEncoder.encode("NewPassword123?") } }
        )
    }

    @Test
    fun `resetPassword returns Err when newPassword y confirmPassword no son iguales`() {
        every { request2.validate() } returns Err(UserError.UserValidationError("Datos de entrada inválidos"))
        every { request2.oldPassword } returns "Password123?"
        every { request2.newPassword } returns "NewPassword123?"
        every { request2.confirmPassword } returns "NewPassword123"
        every { repository.findByGuid(user.guid) } returns user

        val result = service.resetPassword(user.guid, request2)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserValidationError) },
            { assertEquals("Datos de entrada inválidos", (result.error as UserError.UserValidationError).message) },
            { verify { request2.validate() } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun `resetPassword returns Err when oldPassword y newPassword son iguales`() {
        every { request2.validate() } returns Err(UserError.UserValidationError("Datos de entrada inválidos"))
        every { request2.oldPassword } returns "Password123?"
        every { request2.newPassword } returns "Password123?"
        every { request2.confirmPassword } returns "Password123?"
        every { repository.findByGuid(user.guid) } returns user

        val result = service.resetPassword(user.guid, request2)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserValidationError) },
            { assertEquals("Datos de entrada inválidos", (result.error as UserError.UserValidationError).message) },
            { verify { request2.validate() } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun `resetPassword returns Err when user no existe`() {
        val request = mockk<UserPasswordResetRequest>()
        every { request.oldPassword } returns "Password123?"
        every { request.newPassword } returns "NewPassword123?"
        every { request.confirmPassword } returns "NewPassword123?"

        every { repository.findByGuid("guidTestNE") } returns null

        val result = service.resetPassword("guidTestNE", request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserNotFound) },
            { assertEquals("Usuario con GUID guidTestNE no encontrado", (result.error as UserError.UserNotFound).message) },
            { verify { repository.findByGuid("guidTestNE") } },
        )
    }

    @Test
    fun getByCurso() {
        val curso = "curso"
        val users = listOf(user)
        val userResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )
        val expectedResponse = listOf(userResponse)

        every { repository.findByCurso(curso) } returns users
        every { mapper.toUserResponseList(users) } returns expectedResponse

        val result = service.getByCurso(curso)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findByCurso(curso) } },
            { verify { mapper.toUserResponseList(users) } }
        )
    }

    @Test
    fun `getByCurso returns Err when users no existe`() {
        val curso = "cursoNE"

        every { repository.findByCurso(curso) } returns emptyList()

        val result = service.getByCurso(curso)

        assertAll(
            { assertTrue(result.isErr) },
            {
                val error = result.error
                assertTrue(error is UserError.UserNotFound)
                assertEquals("Usuario del curso $curso no encontrado", (error as UserError.UserNotFound).message)
            },
            { verify { repository.findByCurso(curso) } }
        )
    }

    @Test
    fun getByNombre() {
        val expectedResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )

        every { repository.findByNombre(expectedResponse.nombre) } returns user
        every { mapper.toUserResponse(user) } returns expectedResponse

        val result = service.getByNombre(expectedResponse.nombre)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findByNombre(expectedResponse.nombre) } },
            { verify { mapper.toUserResponse(user) } }
        )
    }

    @Test
    fun `getByNombre returns Err when user no existe`() {
        val nombre = "nombreNE"

        every { repository.findByNombre(nombre) } returns null

        val result = service.getByNombre(nombre)

        assertAll(
            { assertTrue(result.isErr) },
            {
                val error = result.error
                assertTrue(error is UserError.UserNotFound)
                assertEquals("Usuario con nombre $nombre no encontrado", (error as UserError.UserNotFound).message)
            },
            { verify { repository.findByNombre(nombre) } }
        )
    }

    @Test
    fun getByEmail() {
        val expectedResponse = UserResponse(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.avatar
        )

        every { repository.findByEmail(expectedResponse.email) } returns user
        every { mapper.toUserResponse(user) } returns expectedResponse

        val result = service.getByEmail(expectedResponse.email)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findByEmail(expectedResponse.email) } },
            { verify { mapper.toUserResponse(user) } }
        )
    }

    @Test
    fun `getByEmail returns Err when user no existe`() {
        val email = "noexiste@loantech.com"

        every { repository.findByEmail(email) } returns null

        val result = service.getByEmail(email)

        assertAll(
            { assertTrue(result.isErr) },
            {
                val error = result.error
                assertTrue(error is UserError.UserNotFound)
                assertEquals("Usuario con email $email no encontrado", (error as UserError.UserNotFound).message)
            },
            { verify { repository.findByEmail(email) } }
        )
    }

    @Test
    fun getByTutor() {
        val tutor = "tutor"
        val userList = listOf(user)
        val responseList = listOf(
            UserResponse(
                user.numeroIdentificacion,
                user.guid,
                user.email,
                user.nombre,
                user.apellidos,
                user.curso!!,
                user.tutor!!,
                user.avatar
            )
        )

        every { repository.findByTutor(tutor) } returns userList
        every { mapper.toUserResponseList(userList) } returns responseList

        val result = service.getByTutor(tutor)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(responseList, result.value) },
            { verify { repository.findByTutor(tutor) } },
            { verify { mapper.toUserResponseList(userList) } }
        )
    }

    @Test
    fun `getByTutor returns Err when user no existe`() {
        val tutor = "noexiste"

        every { repository.findByTutor(tutor) } returns emptyList()

        val result = service.getByTutor(tutor)

        assertAll(
            { assertTrue(result.isErr) },
            {
                val error = result.error
                assertTrue(error is UserError.UserNotFound)
                assertEquals("Usuarios con tutor $tutor no encontrados", (error as UserError.UserNotFound).message)
            },
            { verify { repository.findByTutor(tutor) } }
        )
    }

    @Test
    fun getUserByGuidAdmin() {
        val expectedResponse = UserResponseAdmin(
            user.numeroIdentificacion,
            user.guid,
            user.email,
            user.nombre,
            user.apellidos,
            user.curso!!,
            user.tutor!!,
            user.rol,
            user.isActivo,
            user.createdDate.toString(),
            user.updatedDate.toString(),
            user.lastLoginDate.toString(),
            user.lastPasswordResetDate.toString(),
            false,
            false
        )

        every { repository.findByGuid(expectedResponse.guid) } returns user
        every { mapper.toUserResponseAdmin(user) } returns expectedResponse

        val result = service.getUserByGuidAdmin(expectedResponse.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { verify { repository.findByGuid(expectedResponse.guid) } },
            { verify { mapper.toUserResponseAdmin(user) } }
        )
    }

    @Test
    fun `getUserByGuidAdmin returns Err when user no existe`() {
        val guid = "guidNE"

        every { repository.findByGuid(guid) } returns null

        val result = service.getUserByGuidAdmin(guid)

        assertAll(
            { assertTrue(result.isErr) },
            {
                val error = result.error
                assertTrue(error is UserError.UserNotFound)
                assertEquals("Usuario con GUID $guid no encontrado", (error as UserError.UserNotFound).message)
            },
            { verify { repository.findByGuid(guid) } }
        )
    }

    @Test
    fun getUserById() {
        every { repository.findById(1L) } returns Optional.of(user)

        val result = service.getUserById(1L)

        assertTrue(result.isOk)
        assertEquals(user, result.value)
        verify { repository.findById(1L) }
    }

    @Test
    fun `getUserById returns Err when user no existe`() {
        every { repository.findById(44L) } returns Optional.empty()

        val result = service.getUserById(44L)

        assertTrue(result.isErr)
        assertTrue(result.error is UserError.UserNotFound)
        assertEquals("Usuario con ID 44 no encontrado", (result.error as UserError.UserNotFound).message)
        verify { repository.findById(44L) }
    }

    @Test
    fun updateUser() {
        val request = mockk<UserUpdateRequest>()
        every { request.validate() } returns Ok(request)
        every { request.rol } returns "ADMIN"
        every { request.isActivo } returns false

        every { repository.findByGuid(user.guid) } returns user
        every { repository.save(any()) } returns user
        every { mapper.toUserResponseAdmin(user) } returns mockk()

        val result = service.updateUser(user.guid, request)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(Role.ADMIN, user.rol) },
            { assertFalse(user.isActivo) },
            { verify { repository.findByGuid(user.guid) } },
            { verify { repository.save(user) } },
            { verify { mapper.toUserResponseAdmin(user) } }
        )
    }

    @Test
    fun `updateUser returns Err when es invalido`() {
        val guid = "some-guid"
        val invalidRequest = UserUpdateRequest(rol = "invalidRole", isActivo = true)

        every { repository.findByGuid(guid) } returns mockk()

        val result = service.updateUser(guid, invalidRequest)

        assertTrue(result.isErr)
        assert(result.error is UserError.UserValidationError)
        assert(result.error.message.contains("inválidos"))
    }

    @Test
    fun `updateUser returns Err when user no existe`() {
        val request = mockk<UserUpdateRequest>()
        every { request.rol } returns "ADMIN"
        every { repository.findByGuid("no-guid") } returns null

        val result = service.updateUser("no-guid", request)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserNotFound) },
            { assertEquals("Usuario con GUID no-guid no encontrado", result.error.message) },
            { verify { repository.findByGuid("no-guid") } }
        )
    }

    @Test
    fun derechoAlOlvido() {
        val sanciones = listOf(Sancion())
        val prestamos = listOf(Prestamo())
        val incidencias = listOf(Incidencia(id = 10L))
        val dispositivos = listOf(
            Dispositivo(id = 1L, incidencia = incidencias[0], estadoDispositivo = EstadoDispositivo.NO_DISPONIBLE)
        )

        every { repository.findByGuid(user.guid) } returns user
        every { sancionRepository.findSancionsByUserId(user.id) } returns sanciones
        every { sancionRepository.deleteAll(sanciones) } just Runs

        every { prestamoRepository.findPrestamosByUserId(user.id) } returns prestamos
        every { prestamoRepository.deleteAll(prestamos) } just Runs

        every { incidenciaRepository.findIncidenciasByUserId(user.id) } returns incidencias
        every { dispositivoRepository.findByIncidenciaIdIn(listOf(10L)) } returns dispositivos
        every { dispositivoRepository.saveAll(dispositivos) } returns dispositivos
        every { incidenciaRepository.deleteAll(incidencias) } just Runs

        every { repository.delete(user) } just Runs

        val result = service.derechoAlOlvido(user.guid)

        assertTrue(result.isOk)
        verify {
            repository.findByGuid(user.guid)
            sancionRepository.findSancionsByUserId(user.id)
            sancionRepository.deleteAll(sanciones)
            prestamoRepository.findPrestamosByUserId(user.id)
            prestamoRepository.deleteAll(prestamos)
            incidenciaRepository.findIncidenciasByUserId(user.id)
            dispositivoRepository.findByIncidenciaIdIn(listOf(10L))
            dispositivoRepository.saveAll(dispositivos)
            incidenciaRepository.deleteAll(incidencias)
            repository.delete(user)
        }
    }

    @Test
    fun `derechoAlOlvido returns Err when user no existe`() {
        every { repository.findByGuid("guid404") } returns null

        val result = service.derechoAlOlvido("guid404")

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserNotFound) },
            { assertEquals("Usuario no encontrado con GUID: guid404", result.error.message) },
            { verify { repository.findByGuid("guid404") } }
        )
    }

    @Test
    fun `derechoAlOlvido returns Err when excepción en sanciones`() {
        every { repository.findByGuid(user.guid) } returns user
        every { sancionRepository.findSancionsByUserId(user.id) } throws RuntimeException("DB error")

        val result = service.derechoAlOlvido(user.guid)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.DataBaseError) },
            { assertTrue(result.error.message.contains("sanciones")) }
        )
    }
}