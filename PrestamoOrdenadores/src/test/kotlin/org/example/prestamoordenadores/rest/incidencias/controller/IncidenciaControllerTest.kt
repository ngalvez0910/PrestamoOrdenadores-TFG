package org.example.prestamoordenadores.rest.incidencias.controller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponse
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaResponseAdmin
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError
import org.example.prestamoordenadores.rest.incidencias.services.IncidenciaService
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.storage.pdf.IncidenciaPdfStorage
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
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "admin.admin.loantech@gmail.com", password = "Password123?", roles = ["ADMIN", "ALUMNO", "PROFESOR"])
class IncidenciaControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var service: IncidenciaService

    @MockkBean
    lateinit var pdfStorage: IncidenciaPdfStorage

    @Test
    fun getAllIncidencias() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val incidencia = IncidenciaResponse("guid", "asunto", "descripcion", "PENDIENTE", user, LocalDateTime.now().toString())
        val incidencias = listOf(incidencia)
        val pagedResponse = PagedResponse(
            content = incidencias,
            totalElements = 1,
        )

        every { service.getAllIncidencias(0, 5) } returns Ok(pagedResponse)

        mockMvc.get("/incidencias")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getIncidenciaByGuid() {
        val guid = "123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val incidencia = IncidenciaResponse("guid", "asunto", "descripcion", "PENDIENTE", user, LocalDateTime.now().toString())
        every { service.getIncidenciaByGuid(guid) } returns Ok(incidencia)

        mockMvc.get("/incidencias/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getIncidenciaByGuid returns 404 when no encontrada`() {
        val guid = "123"
        every { service.getIncidenciaByGuid(guid) } returns Err(IncidenciaError.IncidenciaNotFound("No existe"))

        mockMvc.get("/incidencias/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getIncidenciaByGuidAdmin() {
        val guid = "123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val incidencia = IncidenciaResponseAdmin(guid, "asunto", "desc", "PENDIENTE", user, LocalDateTime.now().toString(),
            LocalDateTime.now().toString(), true)
        every { service.getIncidenciaByGuidAdmin(guid) } returns Ok(incidencia)

        mockMvc.get("/incidencias/admin/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getIncidenciaByGuidAdmin returns 404 when no encontrada`() {
        val guid = "123"
        every { service.getIncidenciaByGuidAdmin(guid) } returns Err(IncidenciaError.IncidenciaNotFound("No existe"))

        mockMvc.get("/incidencias/admin/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getIncidenciaByEstado() {
        val estado = "PENDIENTE"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val responseMock = IncidenciaResponse("guid", "asunto", "descripcion", "PENDIENTE", user, LocalDateTime.now().toString())
        every { service.getIncidenciaByEstado(estado) } returns Ok(listOf(responseMock))

        mockMvc.get("/incidencias/estado/$estado")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getIncidenciaByEstado returns 404 when no encontrada`() {
        val estado = "RESUELTO"
        every { service.getIncidenciaByEstado(estado) } returns Err(IncidenciaError.IncidenciaNotFound("No incidencias"))

        mockMvc.get("/incidencias/estado/$estado")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getIncidenciasByUserGuid() {
        val guid = "user-123"
        val user = UserResponse("numIdentificacion", guid, "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val responseMock = IncidenciaResponse("guid", "asunto", "descripcion", "PENDIENTE", user, LocalDateTime.now().toString())
        every { service.getIncidenciasByUserGuid(guid) } returns Ok(listOf(responseMock))

        mockMvc.get("/incidencias/user/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getIncidenciasByUserGuid returns 404 when user no encontrado`() {
        val guid = "user-999"
        every { service.getIncidenciasByUserGuid(guid) } returns Err(IncidenciaError.UserNotFound("Usuario no existe"))

        mockMvc.get("/incidencias/user/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun createIncidencia() {
        val requestJson = """{"asunto":"asunto","descripcion":"desc prueba"}"""
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val responseMock = IncidenciaResponse("guid", "asunto", "desc prueba", "PENDIENTE", user, LocalDateTime.now().toString())
        val incidenciaRequest = IncidenciaCreateRequest("asunto", "desc prueba")
        every { service.createIncidencia(incidenciaRequest) } returns Ok(responseMock)

        mockMvc.post("/incidencias") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `createIncidencia returns 400 on validation error`() {
        val requestJson = """{"asunto":"asunto","descripcion":"desc"}"""
        every { service.createIncidencia(any()) } returns Err(IncidenciaError.IncidenciaValidationError("Error"))

        mockMvc.post("/incidencias") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun updateIncidencia() {
        val guid = "123"
        val requestJson = """{"estadoIncidencia":"RESUELTO"}"""
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val incidenciaRequest = IncidenciaUpdateRequest("RESUELTO")
        val responseMock = IncidenciaResponse(guid, "nuevo asunto", "desc", "RESUELTO", user, LocalDateTime.now().toString())
        every { service.updateIncidencia(eq(guid), incidenciaRequest) } returns Ok(responseMock)

        mockMvc.patch("/incidencias/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `updateIncidencia returns 404 when no encontrada`() {
        val guid = "123"
        val requestJson = """{"estadoIncidencia":"RESUELTO"}"""
        val incidenciaRequest = IncidenciaUpdateRequest("RESUELTO")
        every { service.updateIncidencia(eq(guid), incidenciaRequest) } returns Err(IncidenciaError.IncidenciaNotFound("No existe"))

        mockMvc.patch("/incidencias/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun deleteIncidencia() {
        val guid = "123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val responseMock = IncidenciaResponseAdmin(guid, "asunto", "desc", "PENDIENTE", user, LocalDateTime.now().toString(),
            LocalDateTime.now().toString(), true)
        every { service.deleteIncidenciaByGuid(guid) } returns Ok(responseMock)

        mockMvc.patch("/incidencias/delete/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `deleteIncidencia returns 404 when no encontrada`() {
        val guid = "123"
        every { service.deleteIncidenciaByGuid(guid) } returns Err(IncidenciaError.IncidenciaNotFound("No existe"))

        mockMvc.patch("/incidencias/delete/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun generateAndSavePdf() {
        val guid = "123"
        every { pdfStorage.generateAndSavePdf(guid) } returns Unit

        mockMvc.get("/incidencias/export/pdf/$guid")
            .andExpect {
                status { isOk() }
            }
    }
}