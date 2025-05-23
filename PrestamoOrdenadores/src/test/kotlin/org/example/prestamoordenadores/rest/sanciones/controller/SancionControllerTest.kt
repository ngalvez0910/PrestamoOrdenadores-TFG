package org.example.prestamoordenadores.rest.sanciones.controller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionAdminResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError
import org.example.prestamoordenadores.rest.sanciones.services.SancionService
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import java.time.LocalDate
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "admin.admin.loantech@gmail.com", password = "Password123?", roles = ["ADMIN", "ALUMNO", "PROFESOR"])
class SancionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var service: SancionService

    @Test
    fun getAllSanciones() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionResponse("guid", user, prestamo, "ADVERTENCIA", LocalDate.now().toString(), LocalDate.now().toString())
        val sanciones = listOf(sancion)
        val pagedResponse = PagedResponse(
            content = sanciones,
            totalElements = 1,
        )

        every { service.getAllSanciones(0, 5) } returns Ok(pagedResponse)

        mockMvc.get("/sanciones")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getSancionByGuid() {
        val guid = "abc123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionResponse(guid, user, prestamo, "ADVERTENCIA", LocalDate.now().toString(), LocalDate.now().toString())

        every { service.getSancionByGuid(guid) } returns Ok(sancion)

        mockMvc.get("/sanciones/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getSancionByGuid returns 404 when no encontrada`() {
        every { service.getSancionByGuid("guid") } returns Err(SancionError.SancionNotFound("Error"))

        mockMvc.get("/sanciones/guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getSancionByGuidAdmin() {
        val guid = "admin123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionAdminResponse(
            guid,
            user,
            prestamo,
            "ADVERTENCIA",
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            LocalDateTime.now().toString(),
            LocalDateTime.now().toString(),
            false
        )

        every { service.getSancionByGuidAdmin(guid) } returns Ok(sancion)

        mockMvc.get("/sanciones/admin/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getSancionByFechaSancion() {
        val fecha = LocalDate.now()
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionResponse("guid", user, prestamo, "ADVERTENCIA", fecha.toString(), LocalDate.now().toString())


        every { service.getByFecha(fecha) } returns Ok(listOf(sancion))

        mockMvc.get("/sanciones/fecha/$fecha")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getSancionesByUserGuid() {
        val guid = "user123"
        val user = UserResponse("numIdentificacion", guid, "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionResponse("guid", user, prestamo, "ADVERTENCIA", LocalDate.now().toString(), LocalDate.now().toString())

        every { service.getSancionByUserGuid(guid) } returns Ok(listOf(sancion))

        mockMvc.get("/sanciones/user/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getSancionesByUserGuid should return 404`() {
        val guid = "noexiste"
        every { service.getSancionByUserGuid(guid) } returns Err(SancionError.UserNotFound(""))

        mockMvc.get("/sanciones/user/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getSancionesByTipo() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionResponse("guid", user, prestamo, "ADVERTENCIA", LocalDate.now().toString(), LocalDate.now().toString())

        every { service.getByTipo("ADVERTENCIA") } returns Ok(listOf(sancion))

        mockMvc.get("/sanciones/tipo/ADVERTENCIA")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getSancionesByTipo should return 404`() {
        every { service.getByTipo("tipo") } returns Err(SancionError.SancionNotFound(""))

        mockMvc.get("/sanciones/tipo/tipo")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun updateSancion() {
        val guid = "guid"
        val body = SancionUpdateRequest("BLOQUEO_TEMPORAL")
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionResponse(guid, user, prestamo, "BLOQUEO_TEMPORAL", LocalDate.now().toString(), LocalDate.now().toString())

        every { service.updateSancion(guid, body) } returns Ok(sancion)

        mockMvc.patch("/sanciones/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"tipoSancion": "BLOQUEO_TEMPORAL"}"""
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `updateSancion should return 404 when no encontrada`() {
        val guid = "notfound"
        every { service.updateSancion(guid, any()) } returns Err(SancionError.SancionNotFound(""))

        mockMvc.patch("/sanciones/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"tipoSancion": "nuevo tipo"}"""
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `updateSancion should return 400 when validation error`() {
        val guid = "invalid"
        every { service.updateSancion(guid, any()) } returns Err(SancionError.SancionValidationError(""))

        mockMvc.patch("/sanciones/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"tipo": "invalido"}"""
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun deleteSancion() {
        val guid = "guid"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val sancion = SancionAdminResponse(
            guid,
            user,
            prestamo,
            "ADVERTENCIA",
            LocalDate.now().toString(),
            LocalDate.now().toString(),
            LocalDateTime.now().toString(),
            LocalDateTime.now().toString(),
            false
        )

        every { service.deleteSancionByGuid(guid) } returns Ok(sancion)

        mockMvc.patch("/sanciones/delete/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `deleteSancion should return 404`() {
        val guid = "missing"
        every { service.deleteSancionByGuid(guid) } returns Err(SancionError.SancionNotFound(""))

        mockMvc.patch("/sanciones/delete/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }
}