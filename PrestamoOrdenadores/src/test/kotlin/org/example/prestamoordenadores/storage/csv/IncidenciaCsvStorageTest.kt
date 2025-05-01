package org.example.prestamoordenadores.storage.csv

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertContentEquals

@ExtendWith(MockKExtension::class)
class IncidenciaCsvStorageTest {

    @MockK
    lateinit var repository: IncidenciaRepository

    lateinit var incidenciaCsvStorage: IncidenciaCsvStorage

    var user = User()
    var incidencia = Incidencia()

    @BeforeEach
    fun setUp() {
        user = User(
            id = 99,
            guid = "guidTestU99",
            email = "email99@loantech.com",
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

        incidencia = Incidencia(
            id = 1,
            guid = "INC000000",
            asunto = "asunto",
            descripcion = "descripcion",
            estadoIncidencia = EstadoIncidencia.PENDIENTE,
            user = user,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        incidenciaCsvStorage = IncidenciaCsvStorage(repository)
    }

    @Test
    fun generateCsv() {
        every { repository.findAll() } returns listOf(incidencia)

        val csvData = incidenciaCsvStorage.generateCsv()

        val expectedCsvData = """
            Guid;Asunto;Estado;Usuario;Fecha
            INC000000;asunto;PENDIENTE;guidTestU99;${incidencia.createdDate.toDefaultDateTimeString()}
        """.trimIndent() + "\n"

        val expectedCsvDataBytes = expectedCsvData.toByteArray()

        assertAll(
            { assertContentEquals(expectedCsvDataBytes, csvData) },
            { verify { repository.findAll() } }
        )
    }

    @Test
    fun saveCsvToFile() {
        val csvData = "Guid;Asunto;Estado;Usuario;Fecha\n".toByteArray()
        val fileName = "incidencias_test.csv"

        incidenciaCsvStorage.saveCsvToFile(csvData, fileName)

        val filePath = Paths.get("data", fileName)

        assertAll(
            { assertTrue(Files.exists(filePath)) },
            { assertContentEquals(csvData, Files.readAllBytes(filePath)) }
        )

        Files.delete(filePath)
    }

    @Test
    fun generateAndSaveCsv() {
        val incidenciasMock = listOf(incidencia)
        every { repository.findAll() } returns incidenciasMock

        incidenciaCsvStorage.generateAndSaveCsv()

        val fileName = "incidencias_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        assertTrue(Files.exists(filePath))

        Files.delete(filePath)
    }

}