package org.example.prestamoordenadores.storage.csv

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
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
class DispositivoCsvStorageTest {
    @MockK
    lateinit var repository: DispositivoRepository

    lateinit var dispositivoCsvStorage: DispositivoCsvStorage

    var dispositivo = Dispositivo()

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

        dispositivoCsvStorage = DispositivoCsvStorage(repository)
    }

    @Test
    fun generateCsv() {
        val dispositivosMock = listOf(dispositivo)

        every { repository.findAll() } returns dispositivosMock

        val csvData = dispositivoCsvStorage.generateCsv()

        val expectedCsvData = """
            Guid;Nº Serie;Componentes;Estado;Incidencias;Borrado
            guidTest123;5CD1234XYZ;raton, cargador;DISPONIBLE;;false
        """.trimIndent() + "\n"

        val expectedCsvDataBytes = expectedCsvData.toByteArray()

        assertAll(
            { assertContentEquals(expectedCsvDataBytes, csvData) },
            { verify { repository.findAll() } }
        )
    }

    @Test
    fun saveCsvToFile() {
        val csvData = "Guid;Nº Serie;Componentes;Estado;Incidencias;Borrado\n".toByteArray()
        val fileName = "dispositivos_test.csv"

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
        val dispositivosMock = listOf(dispositivo)
        every { repository.findAll() } returns dispositivosMock

        val dataDir = Paths.get("data")
        Files.createDirectories(dataDir)

        if (Files.exists(dataDir)) {
            Files.list(dataDir).use { stream ->
                stream.forEach { println("  - ${it.fileName}") }
            }
        }

        dispositivoCsvStorage.generateAndSaveCsv()

        Files.list(dataDir).use { stream ->
            stream.forEach { println("  - ${it.fileName}") }
        }

        val fileName = "dispositivos_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        assertTrue(Files.exists(filePath))
    }

}