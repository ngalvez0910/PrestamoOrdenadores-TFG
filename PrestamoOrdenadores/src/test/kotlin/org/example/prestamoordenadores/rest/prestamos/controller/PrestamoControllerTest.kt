package org.example.prestamoordenadores.rest.prestamos.controller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError
import org.example.prestamoordenadores.rest.prestamos.services.PrestamoService
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.storage.pdf.PrestamoPdfStorage
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
import java.time.LocalDate
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "admin.loantech.admin@gmail.com", password = "Password123?", roles = ["ADMIN", "ALUMNO", "PROFESOR"])
class PrestamoControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var service: PrestamoService

    @MockkBean
    lateinit var pdfStorage: PrestamoPdfStorage

    @Test
    fun getAllPrestamos() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val prestamos = listOf(prestamo)
        val pagedResponse = PagedResponse(
            content = prestamos,
            totalElements = 1,
        )

        every { service.getAllPrestamos(0, 5) } returns Ok(pagedResponse)

        mockMvc.get("/prestamos?page=0&size=5")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getPrestamoByGuid() {
        val guid = "prestamo-123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponseAdmin("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString(),
            LocalDateTime.now().toString(), LocalDateTime.now().toString(), false)
        every { service.getPrestamoByGuid(guid) } returns Ok(prestamo)

        mockMvc.get("/prestamos/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getPrestamoByGuid returns 404 when no encontrado`() {
        val guid = "prestamo-999"
        every { service.getPrestamoByGuid(guid) } returns Err(PrestamoError.PrestamoNotFound("No encontrado"))

        mockMvc.get("/prestamos/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun getPrestamoByFechaPrestamo() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val fecha = LocalDateTime.parse(prestamo.fechaPrestamo).toLocalDate()

        every { service.getByFechaPrestamo(fecha) } returns Ok(listOf(prestamo))

        mockMvc.get("/prestamos/fecha/$fecha")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getPrestamoByFechaDevolucion() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val fecha = LocalDateTime.parse(prestamo.fechaDevolucion).toLocalDate()

        every { service.getByFechaDevolucion(fecha) } returns Ok(listOf(prestamo))

        mockMvc.get("/prestamos/devoluciones/$fecha")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun getPrestamosByUserGuid() {
        val guid = "user-123"
        val user = UserResponse("numIdentificacion", guid, "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())

        every { service.getPrestamoByUserGuid(guid) } returns Ok(listOf(prestamo))

        mockMvc.get("/prestamos/user/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getPrestamosByUserGuid returns 404 when user no encontrado`() {
        val guid = "user-999"
        every { service.getPrestamoByUserGuid(guid) } returns Err(PrestamoError.UserNotFound("Usuario no encontrado"))

        mockMvc.get("/prestamos/user/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun createPrestamo() {
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        every { service.createPrestamo() } returns Ok(prestamo)

        mockMvc.post("/prestamos")
            .andExpect {
                status { isCreated() }
            }
    }

    @Test
    fun `createPrestamo returns 404 on user not found`() {
        every { service.createPrestamo() } returns Err(PrestamoError.UserNotFound("Usuario no encontrado"))

        mockMvc.post("/prestamos")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `createPrestamo returns 400 on validation error`() {
        every { service.createPrestamo() } returns Err(PrestamoError.PrestamoValidationError("Inválido"))

        mockMvc.post("/prestamos")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `createPrestamo returns 404 on dispositivo no encontrado`() {
        every { service.createPrestamo() } returns Err(PrestamoError.DispositivoNotFound("No hay dispositivos"))

        mockMvc.post("/prestamos")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun updatePrestamo() {
        val guid = "prestamo-123"
        val requestBody = """{"estadoPrestamo": "VENCIDO"}"""
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "VENCIDO", LocalDateTime.now().toString(), LocalDateTime.now().toString())
        val updateRequest = PrestamoUpdateRequest("VENCIDO")
        every { service.updatePrestamo(guid, updateRequest) } returns Ok(prestamo)

        mockMvc.patch("/prestamos/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `updatePrestamo returns 404 when no encontrado`() {
        val guid = "prestamo-999"
        val updateRequest = PrestamoUpdateRequest("VENCIDO")
        every { service.updatePrestamo(guid, updateRequest) } returns Err(PrestamoError.PrestamoNotFound("No encontrado"))

        mockMvc.patch("/prestamos/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"estadoPrestamo": "VENCIDO"}"""
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `updatePrestamo returns 400 on validation error`() {
        val guid = "prestamo-123"
        val updateRequest = PrestamoUpdateRequest("INVALIDO")
        every { service.updatePrestamo(guid, updateRequest) } returns Err(PrestamoError.PrestamoValidationError("Inválido"))

        mockMvc.patch("/prestamos/$guid") {
            contentType = MediaType.APPLICATION_JSON
            content = """{}"""
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun deletePrestamo() {
        val guid = "prestamo-123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponseAdmin(guid, user, dispositivo, "ENTREGADO", LocalDateTime.now().toString(), LocalDateTime.now().toString(),
            LocalDateTime.now().toString(), LocalDateTime.now().toString(), true)

        every { service.deletePrestamoByGuid(guid) } returns Ok(prestamo)

        mockMvc.patch("/prestamos/delete/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `deletePrestamo returns 404 when no encontrado`() {
        val guid = "prestamo-999"
        every { service.deletePrestamoByGuid(guid) } returns Err(PrestamoError.PrestamoNotFound("No encontrado"))

        mockMvc.patch("/prestamos/delete/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun generateAndSavePdf() {
        val guid = "123"
        val fakePdfBytes = ByteArray(10)
        every { pdfStorage.generatePdf(guid) } returns fakePdfBytes

        mockMvc.get("/prestamos/export/pdf/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun cancelarPrestamo() {
        val guid = "prestamo-123"
        val user = UserResponse("numIdentificacion", "guid", "email.loantech@gmail.com", "nombre", "apellidos", "curso", "tutor", "avatar")
        val dispositivo = DispositivoResponse("guid", "4HJ937MAKO", "cargador")
        val prestamo = PrestamoResponse("guid", user, dispositivo, "DISPONIBLE", LocalDateTime.now().toString(), LocalDateTime.now().toString())

        every { service.cancelarPrestamo(guid) } returns Ok(prestamo)

        mockMvc.patch("/prestamos/cancelar/$guid")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `cancelarPrestamo returns 404 when not found`() {
        val guid = "prestamo-999"
        every { service.cancelarPrestamo(guid) } returns Err(PrestamoError.PrestamoNotFound("No encontrado"))

        mockMvc.patch("/prestamos/cancelar/$guid")
            .andExpect {
                status { isNotFound() }
            }
    }
}