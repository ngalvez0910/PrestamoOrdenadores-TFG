package org.example.prestamoordenadores.storage.excel

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayInputStream
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class PrestamoExcelStorageTest {

    @MockK
    private lateinit var prestamoRepository: PrestamoRepository
    private lateinit var prestamoExcelStorage: PrestamoExcelStorage

    @BeforeEach
    fun setUp() {
        prestamoExcelStorage = PrestamoExcelStorage(prestamoRepository)
    }

    @Test
    fun `generateExcel should create valid Excel with prestamos`() {
        val prestamos = listOf(
            Prestamo(
                guid = "PRES-001",
                user = User(numeroIdentificacion = "2020LT354"),
                dispositivo = Dispositivo(guid= "DS-001"),
                estadoPrestamo = EstadoPrestamo.EN_CURSO,
                fechaPrestamo = LocalDate.of(2023, 5, 1),
                fechaDevolucion = LocalDate.of(2023, 5, 10)
            ),
            Prestamo(
                guid = "PRES-002",
                user = User(numeroIdentificacion = "2020LT028"),
                dispositivo = Dispositivo(guid= "DS-002"),
                estadoPrestamo = EstadoPrestamo.DEVUELTO,
                fechaPrestamo = LocalDate.of(2023, 4, 15),
                fechaDevolucion = LocalDate.of(2023, 4, 20)
            )
        )

        every { prestamoRepository.findAll() } returns prestamos

        val result = prestamoExcelStorage.generateExcel()

        assertNotNull(result)
        assertTrue(result.isNotEmpty())

        val workbook = WorkbookFactory.create(ByteArrayInputStream(result))
        val sheet = workbook.getSheetAt(0)

        val header = sheet.getRow(0)
        assertEquals("Guid", header.getCell(0).stringCellValue)
        assertEquals("Usuario", header.getCell(1).stringCellValue)
        assertEquals("Dispositivo", header.getCell(2).stringCellValue)
        assertEquals("Estado", header.getCell(3).stringCellValue)
        assertEquals("Fecha Préstamo", header.getCell(4).stringCellValue)
        assertEquals("Fecha Devolución", header.getCell(5).stringCellValue)

        val row1 = sheet.getRow(1)
        assertEquals("PRES-001", row1.getCell(0).stringCellValue)
        assertEquals("2020LT354", row1.getCell(1).stringCellValue)
        assertEquals("EN_CURSO", row1.getCell(3).stringCellValue)
        assertEquals("01-05-2023", row1.getCell(4).stringCellValue)
        assertEquals("10-05-2023", row1.getCell(5).stringCellValue)

        val row2 = sheet.getRow(2)
        assertEquals("PRES-002", row2.getCell(0).stringCellValue)
        assertEquals("2020LT028", row2.getCell(1).stringCellValue)
        assertEquals("DEVUELTO", row2.getCell(3).stringCellValue)
        assertEquals("15-04-2023", row2.getCell(4).stringCellValue)
        assertEquals("20-04-2023", row2.getCell(5).stringCellValue)

        workbook.close()
    }

}