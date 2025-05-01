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
            id = 1,
            guid = "guidTestD01",
            numeroSerie = "2ZY098ABCD",
            componentes = "ratón",
            estadoDispositivo = EstadoDispositivo.DISPONIBLE,
            incidencia = null,
            isActivo = true,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        dispositivoCsvStorage = DispositivoCsvStorage(repository)
    }

    @Test
    fun generateCsv() {
        val dispositivosMock = listOf(dispositivo)

        every { repository.findAll() } returns dispositivosMock

        val csvData = dispositivoCsvStorage.generateCsv()

        val expectedCsvData = """
            Guid;Nº Serie;Componentes;Estado;Incidencias;Activo
            guidTestD01;2ZY098ABCD;ratón;DISPONIBLE;;true
        """.trimIndent() + "\n"

        val expectedCsvDataBytes = expectedCsvData.toByteArray()

        assertAll(
            { assertContentEquals(expectedCsvDataBytes, csvData) },
            { verify { repository.findAll() } }
        )
    }

    @Test
    fun saveCsvToFile() {
        val csvData = "Guid;Nº Serie;Componentes;Estado;Incidencias;Activo\n".toByteArray()
        val fileName = "dispositivos_test.csv"

        dispositivoCsvStorage.saveCsvToFile(csvData, fileName)

        val filePath = Paths.get("data", fileName)

        assertAll(
            { assertTrue(Files.exists(filePath)) },
            { assertContentEquals(csvData, Files.readAllBytes(filePath)) }
        )

        Files.delete(filePath)
    }

    @Test
    fun generateAndSaveCsv() {
        val dispositivosMock = listOf(dispositivo)
        every { repository.findAll() } returns dispositivosMock

        dispositivoCsvStorage.generateAndSaveCsv()

        val fileName = "dispositivos_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        assertTrue(Files.exists(filePath))

        Files.delete(filePath)
    }

}