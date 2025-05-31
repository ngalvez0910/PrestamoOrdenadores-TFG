package org.example.prestamoordenadores.storage.excel

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream

@ExtendWith(MockKExtension::class)
class DispositivoExcelStorageTest {

    @MockK
    private lateinit var dispositivoRepository: DispositivoRepository
    private lateinit var dispositivoExcelStorage: DispositivoExcelStorage

    @BeforeEach
    fun setUp() {
        dispositivoExcelStorage = DispositivoExcelStorage(dispositivoRepository)
    }

    @Test
    fun `generateExcel should generate a valid Excel file with dispositivos`() {
        val dispositivos = listOf(
            Dispositivo(
                guid = "1234",
                numeroSerie = "ABC-123",
                componentes = "CPU, RAM",
                estadoDispositivo = EstadoDispositivo.DISPONIBLE,
                incidencia = Incidencia(guid = "5678"),
                isDeleted = false
            ),
            Dispositivo(
                guid = "5678",
                numeroSerie = "XYZ-456",
                componentes = "GPU, SSD",
                estadoDispositivo = EstadoDispositivo.NO_DISPONIBLE,
                incidencia = null,
                isDeleted = true
            )
        )
        every { dispositivoRepository.findAll()} returns dispositivos

        val excelBytes = dispositivoExcelStorage.generateExcel()

        assertNotNull(excelBytes)
        assertTrue(excelBytes.isNotEmpty())

        val inputStream = ByteArrayInputStream(excelBytes)
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)

        val headerRow = sheet.getRow(0)
        assertEquals("Guid", headerRow.getCell(0).stringCellValue)
        assertEquals("Nº Serie", headerRow.getCell(1).stringCellValue)
        assertEquals("Componentes", headerRow.getCell(2).stringCellValue)
        assertEquals("Estado", headerRow.getCell(3).stringCellValue)
        assertEquals("Incidencias", headerRow.getCell(4).stringCellValue)
        assertEquals("Borrado", headerRow.getCell(5).stringCellValue)

        val row1 = sheet.getRow(1)
        assertEquals("1234", row1.getCell(0).stringCellValue)
        assertEquals("ABC-123", row1.getCell(1).stringCellValue)
        assertEquals("CPU, RAM", row1.getCell(2).stringCellValue)
        assertEquals("DISPONIBLE", row1.getCell(3).stringCellValue)
        assertEquals("5678", row1.getCell(4).stringCellValue)
        assertEquals(false, row1.getCell(5).booleanCellValue)

        val row2 = sheet.getRow(2)
        assertEquals("5678", row2.getCell(0).stringCellValue)
        assertEquals("XYZ-456", row2.getCell(1).stringCellValue)
        assertEquals("GPU, SSD", row2.getCell(2).stringCellValue)
        assertEquals("NO_DISPONIBLE", row2.getCell(3).stringCellValue)
        assertEquals("", row2.getCell(4).stringCellValue)
        assertEquals(true, row2.getCell(5).booleanCellValue)

        workbook.close()
    }
}