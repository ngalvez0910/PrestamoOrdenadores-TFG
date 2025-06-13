package org.example.prestamoordenadores.storage.excel

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayInputStream
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class SancionExcelStorageTest {

    @MockK
    private lateinit var sancionRepository: SancionRepository
    private lateinit var sancionExcelStorage: SancionExcelStorage

    @BeforeEach
    fun setUp() {
        sancionExcelStorage = SancionExcelStorage(sancionRepository)
    }

    @Test
    fun `generateExcel should create valid Excel with sanciones`() {
        val sanciones = listOf(
            Sancion(
                guid = "SAN-001",
                user = User(numeroIdentificacion = "2020LT333"),
                tipoSancion = TipoSancion.ADVERTENCIA,
                fechaSancion = LocalDate.of(2023, 3, 1)
            ),
            Sancion(
                guid = "SAN-002",
                user = User(numeroIdentificacion = "2020LT444"),
                tipoSancion = TipoSancion.BLOQUEO_TEMPORAL,
                fechaSancion = LocalDate.of(2023, 3, 15)
            )
        )

        every { sancionRepository.findAll() } returns sanciones

        val result = sancionExcelStorage.generateExcel()

        assertNotNull(result)
        assertTrue(result.isNotEmpty())

        val workbook = WorkbookFactory.create(ByteArrayInputStream(result))
        val sheet = workbook.getSheetAt(0)

        val header = sheet.getRow(0)
        assertEquals("Guid", header.getCell(0).stringCellValue)
        assertEquals("Usuario", header.getCell(1).stringCellValue)
        assertEquals("Tipo de Sanción", header.getCell(2).stringCellValue)
        assertEquals("Fecha de Sanción", header.getCell(3).stringCellValue)

        val row1 = sheet.getRow(1)
        assertEquals("SAN-001", row1.getCell(0).stringCellValue)
        assertEquals("2020LT333", row1.getCell(1).stringCellValue)
        assertEquals("ADVERTENCIA", row1.getCell(2).stringCellValue)
        assertEquals("01-03-2023", row1.getCell(3).stringCellValue)

        val row2 = sheet.getRow(2)
        assertEquals("SAN-002", row2.getCell(0).stringCellValue)
        assertEquals("2020LT444", row2.getCell(1).stringCellValue)
        assertEquals("BLOQUEO_TEMPORAL", row2.getCell(2).stringCellValue)
        assertEquals("15-03-2023", row2.getCell(3).stringCellValue)

        workbook.close()
    }
}