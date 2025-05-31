package org.example.prestamoordenadores.storage.excel

import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "admin.loantech.admin@gmail.com", password = "Password123?", roles = ["ADMIN", "ALUMNO", "PROFESOR"])
class ExcelStorageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockK
    private lateinit var dispositivoExcelStorage: DispositivoExcelStorage

    @MockK
    private lateinit var incidenciaExcelStorage: IncidenciaExcelStorage

    @MockK
    private lateinit var prestamoExcelStorage: PrestamoExcelStorage

    @MockK
    private lateinit var sancionExcelStorage: SancionExcelStorage

    @MockK
    private lateinit var userExcelStorage: UserExcelStorage

    private val dummyExcelData = "FakeExcelData".toByteArray()

    @BeforeEach
    fun setup() {
        every { dispositivoExcelStorage.generateExcel() } returns dummyExcelData
        every { incidenciaExcelStorage.generateExcel() } returns dummyExcelData
        every { prestamoExcelStorage.generateExcel() } returns dummyExcelData
        every { sancionExcelStorage.generateExcel() } returns dummyExcelData
        every { userExcelStorage.generateExcel() } returns dummyExcelData
    }

    @Test
    fun `GET dispositivos returns Excel`() {
        mockMvc.get("/storage/excel/dispositivos")
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
                header { string("Content-Disposition", "attachment; filename=\"dispositivos.xlsx\"") }
                content { contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
            }
    }

    @Test
    fun `GET incidencias returns Excel`() {
        mockMvc.get("/storage/excel/incidencias")
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
                header { string("Content-Disposition", "attachment; filename=\"incidencias.xlsx\"") }
                content { contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
            }
    }

    @Test
    fun `GET prestamos returns Excel`() {
        mockMvc.get("/storage/excel/prestamos")
            .andExpect {
                status { isOk() }
                header { string("Content-Disposition", "attachment; filename=\"prestamos.xlsx\"") }
                content { contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
            }
    }

    @Test
    fun `GET sanciones returns Excel`() {
        mockMvc.get("/storage/excel/sanciones")
            .andExpect {
                status { isOk() }
                header { string("Content-Disposition", "attachment; filename=\"sanciones.xlsx\"") }
                content { contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
            }
    }

    @Test
    fun `GET users returns Excel`() {
        mockMvc.get("/storage/excel/users")
            .andExpect {
                status { isOk() }
                header { string("Content-Disposition", "attachment; filename=\"usuarios.xlsx\"") }
                content { contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
            }
    }
}