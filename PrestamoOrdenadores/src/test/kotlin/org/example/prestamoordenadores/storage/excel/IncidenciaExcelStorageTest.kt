package org.example.prestamoordenadores.storage.excel

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class IncidenciaExcelStorageTest {

    @MockK
    private lateinit var incidenciaRepository: IncidenciaRepository
    private lateinit var incidenciaExcelStorage: IncidenciaExcelStorage

    @BeforeEach
    fun setUp() {
        incidenciaExcelStorage = IncidenciaExcelStorage(incidenciaRepository)
    }

    @Test
    fun `generateExcel should create valid Excel with incidencias`() {
        val now = LocalDateTime.of(2023, 5, 10, 14, 30)

        val incidencias = listOf(
            Incidencia(
                guid = "INC-001",
                asunto = "Pantalla rota",
                estadoIncidencia = EstadoIncidencia.PENDIENTE,
                user = User(numeroIdentificacion = "12345678X"),
                createdDate = now
            ),
            Incidencia(
                guid = "INC-002",
                asunto = "No enciende",
                estadoIncidencia = EstadoIncidencia.RESUELTO,
                user = User(numeroIdentificacion = "87654321Y"),
                createdDate = now.minusDays(2)
            )
        )

        every { incidenciaRepository.findAll() } returns incidencias

        val result = incidenciaExcelStorage.generateExcel()

        assertNotNull(result)
        assertTrue(result.isNotEmpty())

        val workbook = WorkbookFactory.create(ByteArrayInputStream(result))
        val sheet = workbook.getSheetAt(0)

        val header = sheet.getRow(0)
        assertEquals("Guid", header.getCell(0).stringCellValue)
        assertEquals("Asunto", header.getCell(1).stringCellValue)
        assertEquals("Estado", header.getCell(2).stringCellValue)
        assertEquals("Usuario", header.getCell(3).stringCellValue)
        assertEquals("Fecha de Creación", header.getCell(4).stringCellValue)

        val row1 = sheet.getRow(1)
        assertEquals("INC-001", row1.getCell(0).stringCellValue)
        assertEquals("Pantalla rota", row1.getCell(1).stringCellValue)
        assertEquals("PENDIENTE", row1.getCell(2).stringCellValue)
        assertEquals("12345678X", row1.getCell(3).stringCellValue)
        assertEquals(now.toDefaultDateTimeString(), row1.getCell(4).stringCellValue)

        val row2 = sheet.getRow(2)
        assertEquals("INC-002", row2.getCell(0).stringCellValue)
        assertEquals("No enciende", row2.getCell(1).stringCellValue)
        assertEquals("RESUELTO", row2.getCell(2).stringCellValue)
        assertEquals("87654321Y", row2.getCell(3).stringCellValue)
        assertEquals(now.minusDays(2).toDefaultDateTimeString(), row2.getCell(4).stringCellValue)

        workbook.close()
    }

}