package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.dto.UserRoleUpdateRequest
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
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class UserServiceImplTest {
    @MockK
    lateinit var repository: UserRepository

    @MockK
    lateinit var mapper: UserMapper

    @MockK
    lateinit var request: UserAvatarUpdateRequest

    @MockK
    lateinit var request2: UserPasswordResetRequest

    @MockK
    lateinit var request3: UserRoleUpdateRequest

    lateinit var service: UserServiceImpl

    var user : User = User()

    @BeforeEach
    fun setUp() {
        user = User(
            id = 1,
            guid = "guidTest001",
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

        service = UserServiceImpl(repository, mapper)
    }

    @Test
    fun `getAllUsers should return paged response`() {
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
                user.lastPasswordResetDate.toString()
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
        assertEquals("guidTest001", paged.content[0].guid)
        assertEquals(1, paged.totalElements)

        verify { repository.findAll(pageRequest) }
        verify { mapper.toUserResponseListAdmin(userList) }
    }

    @Test
    fun getUserByGuid() {
        val expectedResponse = UserResponse(
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!
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
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!
        )

        every { request.validate() } returns Ok(request)
        every { request.avatar } returns "newAvatar.png"
        every { repository.findByGuid("guidTest001") } returns user
        every { repository.save(any()) } returns user
        every { mapper.toUserResponse(user) } returns expectedResponse

        val result = service.updateAvatar("guidTest001", request)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertEquals("newAvatar.png", user.avatar) },
            { verify { repository.findByGuid("guidTest001") } },
            { verify { repository.save(user) } },
            { verify { mapper.toUserResponse(user) } }
        )
    }

    @Test
    fun `updateAvatar returns Err when request es invalido`() {
        every { request.validate() } returns Err(UserError.UserValidationError("Usuario inválido"))
        every { request.avatar } returns ""

        val result = service.updateAvatar("guidTest001", request)

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
        var user2 = User(
            id = 2,
            guid = "guidTest002",
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

        val expectedResponse = UserResponse(
            guid = user2.guid,
            email = user2.email,
            nombre = user2.nombre,
            apellidos = user2.apellidos,
            curso = user2.curso!!,
            tutor = user2.tutor!!
        )

        every { repository.findByGuid(user2.guid) } returns user2
        every { repository.save(any()) } returns user2
        every { mapper.toUserResponse(user2) } returns expectedResponse

        val result = service.deleteUserByGuid(user2.guid)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertFalse(user2.isActivo) },
            { verify { repository.findByGuid(user2.guid) } },
            { verify { repository.save(user2) } },
            { verify { mapper.toUserResponse(user2) } }
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
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!
        )

        every { request2.validate() } returns Ok(request2)
        every { request2.oldPassword } returns "Password123?"
        every { request2.newPassword } returns "NewPassword123?"
        every { request2.confirmPassword } returns "NewPassword123?"
        every { repository.findByGuid(user.guid) } returns user
        every { repository.save(user) } returns user
        every { mapper.toUserResponse(user) } returns expectedResponse

        val result = service.resetPassword(user.guid, request2)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertEquals("NewPassword123?", user.campoPassword) },
            { verify { request2.validate() } },
            { verify { repository.findByGuid(user.guid) } },
            { verify { repository.save(user) } },
            { verify { mapper.toUserResponse(user) } }
        )
    }

    @Test
    fun `resetPassword returns Err when newPassword y confirmPassword no son iguales`() {
        every { request2.validate() } returns Err(UserError.UserValidationError("Usuario inválido"))
        every { request2.oldPassword } returns "Password123?"
        every { request2.newPassword } returns "NewPassword123?"
        every { request2.confirmPassword } returns "NewPassword123"

        val result = service.resetPassword(user.guid, request2)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserValidationError) },
            { assertEquals("Usuario inválido", (result.error as UserError.UserValidationError).message) },
            { verify { request2.validate() } },
            { verify(exactly = 0) { repository.findByGuid(any()) } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun `resetPassword returns Err when oldPassword y newPassword son iguales`() {
        every { request2.validate() } returns Err(UserError.UserValidationError("Usuario inválido"))
        every { request2.oldPassword } returns "Password123?"
        every { request2.newPassword } returns "Password123?"
        every { request2.confirmPassword } returns "Password123?"

        val result = service.resetPassword(user.guid, request2)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserValidationError) },
            { assertEquals("Usuario inválido", (result.error as UserError.UserValidationError).message) },
            { verify { request2.validate() } },
            { verify(exactly = 0) { repository.findByGuid(any()) } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun `resetPassword returns Err when user no existe`() {
        every { request2.validate() } returns Ok(request2)
        every { request2.oldPassword } returns "Password123?"
        every { request2.newPassword } returns "NewPassword123?"
        every { request2.confirmPassword } returns "NewPassword123?"
        every { repository.findByGuid("guidTestNE") } returns null

        val result = service.resetPassword("guidTestNE", request2)

        assertAll(
            { assertTrue(result.isErr) },
            { assertTrue(result.error is UserError.UserNotFound) },
            { assertEquals("Usuario con GUID ${"guidTestNE"} no encontrado", (result.error as UserError.UserNotFound).message) },
            { verify { request2.validate() } },
            { verify { repository.findByGuid("guidTestNE") } },
            { verify(exactly = 0) { repository.save(any()) } },
            { verify(exactly = 0) { mapper.toUserResponse(any()) } }
        )
    }

    @Test
    fun getByCurso() {
        val curso = "curso"
        val users = listOf(user)
        val userResponse = UserResponse(
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!
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
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!
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
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!
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
                guid = user.guid,
                email = user.email,
                nombre = user.nombre,
                apellidos = user.apellidos,
                curso = user.curso!!,
                tutor = user.tutor!!
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
            user.lastPasswordResetDate.toString()
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
    fun updateRole_ALUMNO() {
        every { request3.validate() } returns Ok(request3)
        every { request3.rol } returns "ALUMNO"

        val expectedUser = User(rol = Role.ALUMNO)
        val expectedResponse = UserResponseAdmin(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!,
            rol = Role.ALUMNO,
            isActivo = true,
            createdDate = user.createdDate.toString(),
            updatedDate = user.updatedDate.toString(),
            lastLoginDate = user.lastLoginDate.toString(),
            lastPasswordResetDate = user.lastPasswordResetDate.toString()
        )

        every { repository.findByGuid(user.guid) } returns user
        every { repository.save(any()) } returns expectedUser
        every { mapper.toUserResponseAdmin(user) } returns expectedResponse

        val result = service.updateRole(user.guid, request3)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertEquals(Role.ALUMNO, user.rol) },
            { verify { repository.findByGuid(user.guid) } },
            { verify { repository.save(user) } },
            { verify { mapper.toUserResponseAdmin(user) } }
        )
    }

    @Test
    fun updateRole_PROFESOR() {
        every { request3.validate() } returns Ok(request3)
        every { request3.rol } returns "PROFESOR"

        val expectedUser = User(rol = Role.PROFESOR)
        val expectedResponse = UserResponseAdmin(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!,
            rol = Role.PROFESOR,
            isActivo = true,
            createdDate = user.createdDate.toString(),
            updatedDate = user.updatedDate.toString(),
            lastLoginDate = user.lastLoginDate.toString(),
            lastPasswordResetDate = user.lastPasswordResetDate.toString()
        )

        every { repository.findByGuid(user.guid) } returns user
        every { repository.save(any()) } returns expectedUser
        every { mapper.toUserResponseAdmin(user) } returns expectedResponse

        val result = service.updateRole(user.guid, request3)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertEquals(Role.PROFESOR, user.rol) },
            { verify { repository.findByGuid(user.guid) } },
            { verify { repository.save(user) } },
            { verify { mapper.toUserResponseAdmin(user) } }
        )
    }

    @Test
    fun updateRole_ADMIN() {
        every { request3.validate() } returns Ok(request3)
        every { request3.rol } returns "ADMIN"

        val expectedUser = User(rol = Role.ADMIN)
        val expectedResponse = UserResponseAdmin(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso!!,
            tutor = user.tutor!!,
            rol = Role.ADMIN,
            isActivo = true,
            createdDate = user.createdDate.toString(),
            updatedDate = user.updatedDate.toString(),
            lastLoginDate = user.lastLoginDate.toString(),
            lastPasswordResetDate = user.lastPasswordResetDate.toString()
        )

        every { repository.findByGuid(user.guid) } returns user
        every { repository.save(any()) } returns expectedUser
        every { mapper.toUserResponseAdmin(user) } returns expectedResponse

        val result = service.updateRole(user.guid, request3)

        assertAll(
            { assertTrue(result.isOk) },
            { assertEquals(expectedResponse, result.value) },
            { assertEquals(Role.ADMIN, user.rol) },
            { verify { repository.findByGuid(user.guid) } },
            { verify { repository.save(user) } },
            { verify { mapper.toUserResponseAdmin(user) } }
        )
    }

    @Test
    fun `updateRole returns Err when user no existe`() {
        every { request3.validate() } returns Ok(request3)
        every { request3.rol } returns "ADMIN"

        every { repository.findByGuid("guidNE") } returns null

        val result = service.updateRole("guidNE", request3)

        assertTrue(result.isErr)
        assertTrue(result.error is UserError.UserNotFound)
    }

    @Test
    fun `updateRole returns Err when request es invalido`() {
        every { request3.validate() } returns Err(UserError.UserValidationError("Usuario inválido"))
        every { request3.rol } returns "ADMINISTRADOR"

        val result = service.updateRole(user.guid, request3)

        assertTrue(result.isErr)
        assertTrue(result.error is UserError.UserValidationError)
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
}