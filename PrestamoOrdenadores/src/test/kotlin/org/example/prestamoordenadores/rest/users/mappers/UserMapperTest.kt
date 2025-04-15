package org.example.prestamoordenadores.rest.users.mappers

import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UserMapperTest {
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

    private val mapper = UserMapper()

    @Test
    fun toUserResponse() {
        val userResponse = UserResponse(
            "guidTest123",
            "email",
            "name",
            "apellido",
            "curso",
            "tutor"
        )

        val response = mapper.toUserResponse(user)

        assertAll(
            { assertEquals(userResponse.guid, response.guid) },
            { assertEquals(userResponse.email, response.email) },
            { assertEquals(userResponse.nombre, response.nombre) },
            { assertEquals(userResponse.curso, response.curso) }
        )
    }

    @Test
    fun toUserResponseAdmin() {
        val userResponseAdmin = UserResponseAdmin(
            "numIdent",
            "guidTest123",
            "email",
            "name",
            "apellido",
            "curso",
            "tutor",
            Role.ALUMNO,
            true,
            LocalDateTime.now().toString(),
            LocalDateTime.now().toString(),
            LocalDateTime.now().toString(),
            LocalDateTime.now().toString()
        )

        val response = mapper.toUserResponseAdmin(user)

        assertAll(
            { assertEquals(userResponseAdmin.guid, response.guid) },
            { assertEquals(userResponseAdmin.email, response.email) },
            { assertEquals(userResponseAdmin.nombre, response.nombre) },
            { assertEquals(userResponseAdmin.rol, response.rol) },
            { assertEquals(userResponseAdmin.isActivo, response.isActivo) }
        )
    }

    @Test
    fun toUserFromCreate() {
        val userCreate = UserCreateRequest(
            "numIdent",
            "name",
            "apellido",
            "email",
            "curso",
            "tutor",
            "password",
            "password"
        )

        val response = mapper.toUserFromCreate(userCreate)

        assertAll(
            { assertEquals(userCreate.numeroIdentificacion, response.numeroIdentificacion) },
            { assertEquals(userCreate.nombre, response.nombre) },
            { assertEquals(userCreate.email, response.email) },
            { assertEquals(userCreate.password, response.campoPassword) }
        )
    }

    @Test
    fun toUserResponseList() {
        val users = listOf(user)

        val responses = mapper.toUserResponseList(users)

        assertAll(
            { assertEquals(1, responses.size) },
            { assertEquals(user.guid, responses[0].guid) },
            { assertEquals(user.email, responses[0].email) },
            { assertEquals(user.nombre, responses[0].nombre) }
        )
    }

    @Test
    fun `toUserResponseList lista vacia`() {
        val users = emptyList<User>()

        val responses = mapper.toUserResponseList(users)

        assertTrue(responses.isEmpty())
    }

    @Test
    fun toUserResponseListAdmin() {
        val users = listOf(user)

        val responses = mapper.toUserResponseListAdmin(users)

        assertAll(
            { assertEquals(1, responses.size) },
            { assertEquals(user.guid, responses[0].guid) },
            { assertEquals(user.email, responses[0].email) },
            { assertEquals(user.rol, responses[0].rol) }
        )
    }

    @Test
    fun `toUserResponseListAdmin lista vacia`() {
        val users = emptyList<User>()

        val responses = mapper.toUserResponseListAdmin(users)

        assertTrue(responses.isEmpty())
    }

}