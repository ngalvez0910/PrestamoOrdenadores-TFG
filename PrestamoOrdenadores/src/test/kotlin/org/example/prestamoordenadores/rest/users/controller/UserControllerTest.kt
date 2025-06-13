package org.example.prestamoordenadores.rest.users.controller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.example.prestamoordenadores.PrestamoOrdenadoresApplication
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.dto.UserUpdateRequest
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.services.UserService
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.put
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest(classes = [PrestamoOrdenadoresApplication::class])
@Testcontainers
@WithMockUser(username = "admin.loantech.admin@gmail.com", password = "Password123?", roles = ["ADMIN", "ALUMNO", "PROFESOR"])
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var service: UserService

    companion object {
        @Container
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer("postgres:15.3")
            .withDatabaseName("prestamosDB-test")
            .withUsername("testuser")
            .withPassword("testpass")
    }

    @Test
    fun getAllUsers() {
        val user = UserResponseAdmin("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", Role.ADMIN, true, LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), false, false)
        val users = listOf(user)
        val pagedResponse = PagedResponse(
            content = users,
            totalElements = 1,
        )

        every { service.getAllUsers(0, 5) } returns Ok(pagedResponse)

        mockMvc.get("/users")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getUserByGuid() {
        val guid = "guid123"
        val user = UserResponse("numIdentificacion", guid, "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")

        every { service.getUserByGuid(guid) } returns Ok(user)

        mockMvc.get("/users/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getUserByGuid returns 404 when no encontrado`() {
        every { service.getUserByGuid("abc") } returns Err(UserError.UserNotFound("no user"))

        mockMvc.get("/users/abc")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun getUserByNombre() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")

        every { service.getByNombre("nombre") } returns Ok(user)

        mockMvc.get("/users/nombre/nombre")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getUserByNombre returns 404 when no encontrado`() {
        every { service.getByNombre("abc") } returns Err(UserError.UserNotFound("no user"))

        mockMvc.get("/users/nombre/abc")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun getUsersByGrade() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")

        every { service.getByCurso("curso") } returns Ok(listOf(user))

        mockMvc.get("/users/curso/curso")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getUsersByGrade returns 404 when no encontrado`() {
        every { service.getByCurso("abc") } returns Err(UserError.UserNotFound("no user"))

        mockMvc.get("/users/curso/abc")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun getUserByEmail() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")

        every { service.getByEmail("email.loantech@gmail.com") } returns Ok(user)

        mockMvc.get("/users/email/email.loantech@gmail.com")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getUserByEmail returns 404 when no encontrado`() {
        every { service.getByEmail("abc") } returns Err(UserError.UserNotFound("no user"))

        mockMvc.get("/users/email/abc")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun getUsersByTutor() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")

        every { service.getByTutor("tutor") } returns Ok(listOf(user))

        mockMvc.get("/users/tutor/tutor")
            .andExpect { status { isOk() } }
    }

    @Test
    fun `getUsersByTutor returns 404 when no encontrado`() {
        every { service.getByTutor("abc") } returns Err(UserError.UserNotFound("no user"))

        mockMvc.get("/users/tutor/abc")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun updateAvatar() {
        val body = """{"avatar":"url"}"""
        val request = UserAvatarUpdateRequest("url")
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "url")

        every { service.updateAvatar("guid", request) } returns Ok(user)

        mockMvc.patch("/users/avatar/guid") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun resetPassword() {
        val body = """{"oldPassword":"oldPassword1?", "newPassword":"nuevaPassword1?", "confirmPassword":"nuevaPassword1?"}"""
        val request = UserPasswordResetRequest("oldPassword1?", "nuevaPassword1?", "nuevaPassword1?")
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")

        every { service.resetPassword("guid", request) } returns Ok(user)

        mockMvc.patch("/users/password/guid") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect { status { isOk() } }
    }

    @Test
    fun deleteUserByGuid() {
        val user = UserResponseAdmin("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", Role.ADMIN, false, LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), true, false)

        every { service.deleteUserByGuid("guid") } returns Ok(user)

        mockMvc.patch("/users/delete/guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `deleteUserByGuid returns 404 when no encontrado`() {
        every { service.deleteUserByGuid("abc") } returns Err(UserError.UserNotFound("no user"))

        mockMvc.patch("/users/delete/abc")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun getUserByGuidAdmin() {
        val guid = "guid123"
        val user = UserResponseAdmin("numIdentificacion", guid, "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", Role.ADMIN, true, LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), false, false)

        every { service.getUserByGuidAdmin(guid) } returns Ok(user)

        mockMvc.get("/users/admin/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getUserByGuidAdmin returns 404 when no encontrado`() {
        every { service.getUserByGuidAdmin("abc") } returns Err(UserError.UserNotFound("no user"))

        mockMvc.get("/users/admin/abc")
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun updateUser() {
        val body = """{"rol":"ALUMNO", "isActivo":"true"}"""
        val request = UserUpdateRequest("ALUMNO", true)
        val user = UserResponseAdmin("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", Role.ALUMNO, true, LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), false, false)

        every { service.updateUser("guid", request) } returns Ok(user)

        mockMvc.put("/users/guid") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect { status { isOk() } }
    }

    @Test
    fun derechoAlOlvido() {
        every { service.derechoAlOlvido("guid") } returns Ok(Unit)

        mockMvc.delete("/users/derechoOlvido/guid")
            .andExpect {
                status { isOk() }
            }
    }
}