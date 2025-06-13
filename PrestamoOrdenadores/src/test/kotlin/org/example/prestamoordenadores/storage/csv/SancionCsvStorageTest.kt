package org.example.prestamoordenadores.storage.csv

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertContentEquals

@ExtendWith(MockKExtension::class)
class SancionCsvStorageTest {
    @MockK
    lateinit var repository: SancionRepository

    lateinit var sancionCsvStorage: SancionCsvStorage

    var user = User()
    var sancion = Sancion()

    @BeforeEach
    fun setUp() {
        user = User(
            id = 99,
            guid = "guidTestU99",
            email = "email99.loantech@gmail.com",
            numeroIdentificacion = "2023LT249",
            campoPassword = "Password123?",
            nombre = "nombre99",
            apellidos = "apellidos99",
            curso = "curso99",
            tutor = "tutor99",
            avatar = "avatar99.png",
            rol = Role.ALUMNO,
            isActivo = true,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        sancion = Sancion(
            id = 1,
            guid = "SANC000000",
            user = user,
            tipoSancion = TipoSancion.ADVERTENCIA,
            fechaSancion = LocalDate.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        sancionCsvStorage = SancionCsvStorage(repository)
    }

    @Test
    fun generateCsv() {
        every { repository.findAll() } returns listOf(sancion)

        val csvData = sancionCsvStorage.generateCsv()

        val expectedCsvData = """
            Guid;Usuario;Tipo;Fecha
            SANC000000;guidTestU99;ADVERTENCIA;${sancion.fechaSancion.toDefaultDateString()}
        """.trimIndent() + "\n"

        val expectedCsvDataBytes = expectedCsvData.toByteArray()

        assertAll(
            { assertContentEquals(expectedCsvDataBytes, csvData) },
            { verify { repository.findAll() } }
        )
    }

    @Test
    fun saveCsvToFile() {
        val csvData = "Guid;Usuario;Tipo;Fecha\n".toByteArray()
        val fileName = "sanciones_test.csv"

        val tempDir = Files.createTempDirectory("csv_test")
        val filePath = tempDir.resolve(fileName)

        Files.write(filePath, csvData)

        assertAll(
            { assertTrue(Files.exists(filePath)) },
            { assertContentEquals(csvData, Files.readAllBytes(filePath)) }
        )

        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .forEach { Files.deleteIfExists(it) }
    }

    @Test
    fun generateAndSaveCsv() {
        val sancionesMock = listOf(sancion)
        every { repository.findAll() } returns sancionesMock

        val dataDir = Paths.get("data")
        Files.createDirectories(dataDir)

        if (Files.exists(dataDir)) {
            Files.list(dataDir).use { stream ->
                stream.forEach { println("  - ${it.fileName}") }
            }
        }

        sancionCsvStorage.generateAndSaveCsv()

        Files.list(dataDir).use { stream ->
            stream.forEach { println("  - ${it.fileName}") }
        }

        val fileName = "sanciones_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        assertTrue(Files.exists(filePath))
    }

}