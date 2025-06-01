package org.example.prestamoordenadores.storage.csv

import com.github.michaelbull.result.Err
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.example.prestamoordenadores.PrestamoOrdenadoresApplication
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.rest.users.services.CustomUserDetailsService
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@WithMockUser(username = "admin.loantech.admin@gmail.com", password = "Password123?", roles = ["ADMIN", "ALUMNO", "PROFESOR"])
class CsvStorageControllerTest {

    companion object {
        @Container
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("prestamosDB-test")
            .withUsername("admin")
            .withPassword("adminPassword123")
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var dispositivoCsvStorage: DispositivoCsvStorage

    @MockkBean
    lateinit var incidenciaCsvStorage: IncidenciaCsvStorage

    @MockkBean
    lateinit var prestamoCsvStorage: PrestamoCsvStorage

    @MockkBean
    lateinit var sancionCsvStorage: SancionCsvStorage

    @MockkBean
    lateinit var userCsvStorage: UserCsvStorage

    private val today = LocalDate.now()
    private val dataDir = Paths.get("data")

    @BeforeEach
    fun setup() {
        Files.createDirectories(dataDir)
    }

    private fun createFile(fileName: String) {
        val filePath = dataDir.resolve(fileName)
        Files.write(filePath, "test,data\n1,abc".toByteArray())
    }

    private fun deleteFile(fileName: String) {
        Files.deleteIfExists(dataDir.resolve(fileName))
    }

    @Test
    fun exportCsvDispositivos() {
        val fileName = "dispositivos_${today.toDefaultDateString()}.csv"
        val fichero = createFile(fileName)

        every { dispositivoCsvStorage.generateAndSaveCsv() } returns fichero

        mockMvc.get("/storage/csv/dispositivos")
            .andExpect {
                status { isOk() }
                header { string(HttpHeaders.CONTENT_TYPE, "text/csv") }
                header { string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName") }
            }

        deleteFile(fileName)
    }

    @Test
    fun exportCsvIncidencias() {
        val fileName = "incidencias_${today.toDefaultDateString()}.csv"
        val fichero = createFile(fileName)

        every { incidenciaCsvStorage.generateAndSaveCsv() } returns fichero

        mockMvc.get("/storage/csv/incidencias")
            .andExpect {
                status { isOk() }
                header { string(HttpHeaders.CONTENT_TYPE, "text/csv") }
                header { string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName") }
            }

        deleteFile(fileName)
    }

    @Test
    fun exportCsvPrestamos() {
        val fileName = "prestamos_${today.toDefaultDateString()}.csv"
        val fichero = createFile(fileName)

        every { prestamoCsvStorage.generateAndSaveCsv() } returns fichero

        mockMvc.get("/storage/csv/prestamos")
            .andExpect {
                status { isOk() }
            }

        deleteFile(fileName)
    }

    @Test
    fun exportCsvSanciones() {
        val fileName = "sanciones_${today.toDefaultDateString()}.csv"
        val fichero = createFile(fileName)

        every { sancionCsvStorage.generateAndSaveCsv() } returns fichero

        mockMvc.get("/storage/csv/sanciones")
            .andExpect {
                status { isOk() }
            }

        deleteFile(fileName)
    }

    @Test
    fun exportCsvUsers() {
        val fileName = "usuarios_${today.toDefaultDateString()}.csv"
        val fichero = createFile(fileName)

        every { userCsvStorage.generateAndSaveCsv() } returns fichero

        mockMvc.get("/storage/csv/users")
            .andExpect {
                status { isOk() }
                header { string(HttpHeaders.CONTENT_TYPE, "text/csv") }
                header { string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName") }
            }

        deleteFile(fileName)
    }

}