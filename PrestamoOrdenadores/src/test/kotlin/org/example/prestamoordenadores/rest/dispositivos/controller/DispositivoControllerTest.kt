package org.example.prestamoordenadores.rest.dispositivos.controller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.rest.dispositivos.services.DispositivoService
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.junit.jupiter.api.BeforeEach
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

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "admin.admin.loantech@gmail.com", password = "Password123?", roles = ["ADMIN", "ALUMNO", "PROFESOR"])
class DispositivoControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var service: DispositivoService

    @Test
    fun getAllDispositivos() {
        val dispositivo = DispositivoResponseAdmin("guid", "4HJ937MAKO", "cargador", "DISPONIBLE", null, false)
        val dispositivos = listOf(dispositivo)
        val pagedResponse = PagedResponse(
            content = dispositivos,
            totalElements = 1,
        )

        every { service.getAllDispositivos(0, 5) } returns Ok(pagedResponse)

        mockMvc.get("/dispositivos")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getDispositivoByGuid() {
        val guid = "123"
        val dispositivo = DispositivoResponseAdmin(guid, "4HJ937MAKO", "cargador", "DISPONIBLE", null, false)
        every { service.getDispositivoByGuid(guid) } returns Ok(dispositivo)

        mockMvc.get("/dispositivos/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getDispositivoByGuid returns 404 when no encontrado`() {
        val guid = "notfound"
        every { service.getDispositivoByGuid(guid) } returns Err(DispositivoError.DispositivoNotFound("mensaje"))

        mockMvc.get("/dispositivos/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getDispositivoByNumeroSerie() {
        val dispositivo = DispositivoResponseAdmin("guid", "4HJ937MAKO", "cargador", "DISPONIBLE", null, false)
        every { service.getDispositivoByNumeroSerie("4HJ937MAKO") } returns Ok(dispositivo)

        mockMvc.get("/dispositivos/numeroSerie/4HJ937MAKO")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getDispositivoByNumeroSerie returns 404 when no encontrado`() {
        val numeroSerie = "ABC123"
        every { service.getDispositivoByNumeroSerie(numeroSerie) } returns Err(DispositivoError.DispositivoNotFound("mensaje"))

        mockMvc.get("/dispositivos/numeroSerie/$numeroSerie")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getDispositivoByEstado() {
        val estado = "DISPONIBLE"
        val dispositivo = DispositivoResponseAdmin("guid", "4HJ937MAKO", "cargador", "DISPONIBLE", null, false)
        every { service.getDispositivoByEstado(estado) } returns Ok(listOf(dispositivo))

        mockMvc.get("/dispositivos/estado/$estado")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getDispositivoByEstado returns 404 when no encontrado`() {
        every { service.getDispositivoByEstado("ELIMINADO") } returns Err(DispositivoError.DispositivoNotFound("mensaje"))

        mockMvc.get("/dispositivos/estado/ELIMINADO")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun createDispositivo() {
        val request = """{"numeroSerie": "4HJ937MAKO", "componentes": "cargador"}"""
        val dispositivoRequest = DispositivoCreateRequest("4HJ937MAKO", "cargador")
        val dispositivo = DispositivoResponse("guid","4HJ937MAKO", "cargador")
        every { service.createDispositivo(dispositivoRequest) } returns Ok(dispositivo)

        mockMvc.post("/dispositivos") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `createDispositivo returns 400 on validation error`() {
        val body = """{"nombre": "test"}"""
        every { service.createDispositivo(any()) } returns Err(DispositivoError.DispositivoValidationError("mensaje"))

        mockMvc.post("/dispositivos") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun updateDispositivo() {
        val guid = "123"
        val requestBody = """{"estado": "DISPONIBLE"}"""
        val dispositivoResponse = DispositivoResponseAdmin(guid, "4HJ937MAKO", "cargador", "DISPONIBLE", null, false)

        every { service.updateDispositivo(guid, any()) } returns Ok(dispositivoResponse)

        mockMvc.patch("/dispositivos/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `updateDispositivo returns 404 when no encontrado`() {
        val guid = "123"
        val body = """{"estado": "DISPONIBLE"}"""
        every { service.updateDispositivo(eq(guid), any()) } returns Err(DispositivoError.DispositivoNotFound("mensaje"))

        mockMvc.patch("/dispositivos/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun deleteDispositivo() {
        val guid = "123"
        val dispositivo = DispositivoResponseAdmin(guid, "4HJ937MAKO", "cargador", "DISPONIBLE", null, false)
        every { service.deleteDispositivoByGuid(guid) } returns Ok(dispositivo)

        mockMvc.patch("/dispositivos/delete/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getStock() {
        every { service.getStock() } returns Ok(10)

        mockMvc.get("/dispositivos/stock")
            .andExpect {
                status { isOk() }
            }
    }
}