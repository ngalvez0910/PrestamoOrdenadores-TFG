package org.example.prestamoordenadores.storage.csv

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
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
class UserCsvStorageTest {
    @MockK
    lateinit var repository: UserRepository

    lateinit var userCsvStorage: UserCsvStorage

    var user = User()

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

        userCsvStorage = UserCsvStorage(repository)
    }

    @Test
    fun generateCsv() {
        every { repository.findAll() } returns listOf(user)

        val csvData = userCsvStorage.generateCsv()

        val expectedCsvData = """
            Guid;Email;Nombre;Apellidos;Curso;Tutor;Rol;Activo
            guidTestU99;email99.loantech@gmail.com;nombre99;apellidos99;curso99;tutor99;ALUMNO;true
        """.trimIndent() + "\n"

        val expectedCsvDataBytes = expectedCsvData.toByteArray()

        assertAll(
            { assertContentEquals(expectedCsvDataBytes, csvData) },
            { verify { repository.findAll() } }
        )
    }

    @Test
    fun saveCsvToFile() {
        val csvData = "Guid;Email;Nombre;Apellidos;Curso;Tutor;Rol;Activo\n".toByteArray()
        val fileName = "usuarios_test.csv"

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
        val usersMock = listOf(user)
        every { repository.findAll() } returns usersMock

        val dataDir = Paths.get("dataTest")
        Files.createDirectories(dataDir)

        if (Files.exists(dataDir)) {
            Files.list(dataDir).use { stream ->
                stream.forEach { println("  - ${it.fileName}") }
            }
        }

        userCsvStorage.generateAndSaveCsv()

        Files.list(dataDir).use { stream ->
            stream.forEach { println("  - ${it.fileName}") }
        }

        val fileName = "usuarios_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("dataTest", fileName)

        assertTrue(Files.exists(filePath))
    }
}