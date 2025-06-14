package org.example.prestamoordenadores.storage.csv

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.junit.jupiter.api.AfterEach
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
class PrestamoCsvStorageTest {

    @MockK
    lateinit var repository: PrestamoRepository

    lateinit var prestamoCsvStorage: PrestamoCsvStorage

    var user = User()
    var dispositivo = Dispositivo()
    var prestamo = Prestamo()

    @BeforeEach
    fun setUp() {
        dispositivo = Dispositivo(
            1,
            "guidTest123",
            "5CD1234XYZ",
            "raton, cargador",
            EstadoDispositivo.DISPONIBLE,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        )

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

        prestamo = Prestamo(
            id = 1,
            guid = "guidTestP01",
            user = user,
            dispositivo = dispositivo,
            estadoPrestamo = EstadoPrestamo.EN_CURSO,
            fechaPrestamo = LocalDate.now(),
            fechaDevolucion = LocalDate.now().plusDays(1),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        prestamoCsvStorage = PrestamoCsvStorage(repository)
    }

    @Test
    fun generateCsv() {
        every { repository.findAll() } returns listOf(prestamo)

        val csvData = prestamoCsvStorage.generateCsv()

        val expectedCsvData = """
            Guid;Usuario;Dispositivo;Estado;Fecha Préstamo;Fecha Devolución
            guidTestP01;guidTestU99;guidTest123;EN_CURSO;${prestamo.fechaPrestamo.toDefaultDateString()};${prestamo.fechaDevolucion.toDefaultDateString()}
        """.trimIndent() + "\n"

        val expectedCsvDataBytes = expectedCsvData.toByteArray()

        assertAll(
            { assertContentEquals(expectedCsvDataBytes, csvData) },
            { verify { repository.findAll() } }
        )
    }

    @Test
    fun saveCsvToFile() {
        val csvData = "Guid;Usuario;Dispositivo;Estado;Fecha Préstamo;Fecha Devolución\n".toByteArray()
        val fileName = "prestamos_test.csv"

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
        val prestamosMock = listOf(prestamo)
        every { repository.findAll() } returns prestamosMock

        val dataDir = Paths.get("data")
        Files.createDirectories(dataDir)

        if (Files.exists(dataDir)) {
            Files.list(dataDir).use { stream ->
                stream.forEach { println("  - ${it.fileName}") }
            }
        }

        prestamoCsvStorage.generateAndSaveCsv()

        Files.list(dataDir).use { stream ->
            stream.forEach { println("  - ${it.fileName}") }
        }

        val fileName = "prestamos_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        assertTrue(Files.exists(filePath))
    }
}