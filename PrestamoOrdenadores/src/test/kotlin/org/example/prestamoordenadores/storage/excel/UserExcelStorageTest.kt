package org.example.prestamoordenadores.storage.excel

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayInputStream

@ExtendWith(MockKExtension::class)
class UserExcelStorageTest {

    @MockK
    private lateinit var userRepository: UserRepository
    private lateinit var userExcelStorage: UserExcelStorage

    @BeforeEach
    fun setUp() {
        userExcelStorage = UserExcelStorage(userRepository)
    }

    @Test
    fun `generateExcel should create valid Excel with users`() {
        val users = listOf(
            User(
                numeroIdentificacion = "11111111A",
                email = "juan@example.com",
                nombre = "Juan",
                apellidos = "Pérez",
                curso = "1º ESO",
                tutor = "Tutora 1",
                rol = Role.ALUMNO,
                isActivo = true
            ),
            User(
                numeroIdentificacion = "22222222B",
                email = "ana@example.com",
                nombre = "Ana",
                apellidos = "García",
                curso = null,
                tutor = null,
                rol = Role.PROFESOR,
                isActivo = false
            )
        )

        every { userRepository.findAll() } returns users

        val result = userExcelStorage.generateExcel()

        assertNotNull(result)
        assertTrue(result.isNotEmpty())

        val workbook = WorkbookFactory.create(ByteArrayInputStream(result))
        val sheet = workbook.getSheetAt(0)

        val header = sheet.getRow(0)
        assertEquals("Número de identificación", header.getCell(0).stringCellValue)
        assertEquals("Email", header.getCell(1).stringCellValue)
        assertEquals("Nombre", header.getCell(2).stringCellValue)
        assertEquals("Apellidos", header.getCell(3).stringCellValue)
        assertEquals("Curso", header.getCell(4).stringCellValue)
        assertEquals("Tutor", header.getCell(5).stringCellValue)
        assertEquals("Rol", header.getCell(6).stringCellValue)
        assertEquals("Activo", header.getCell(7).stringCellValue)

        val row1 = sheet.getRow(1)
        assertEquals("11111111A", row1.getCell(0).stringCellValue)
        assertEquals("juan@example.com", row1.getCell(1).stringCellValue)
        assertEquals("Juan", row1.getCell(2).stringCellValue)
        assertEquals("Pérez", row1.getCell(3).stringCellValue)
        assertEquals("1º ESO", row1.getCell(4).stringCellValue)
        assertEquals("Tutora 1", row1.getCell(5).stringCellValue)
        assertEquals("ALUMNO", row1.getCell(6).stringCellValue)
        assertTrue(row1.getCell(7).booleanCellValue)

        val row2 = sheet.getRow(2)
        assertEquals("22222222B", row2.getCell(0).stringCellValue)
        assertEquals("ana@example.com", row2.getCell(1).stringCellValue)
        assertEquals("Ana", row2.getCell(2).stringCellValue)
        assertEquals("García", row2.getCell(3).stringCellValue)
        assertEquals("", row2.getCell(4).stringCellValue)
        assertEquals("", row2.getCell(5).stringCellValue)
        assertEquals("PROFESOR", row2.getCell(6).stringCellValue)
        assertEquals(false, row2.getCell(7).booleanCellValue)

        workbook.close()
    }
}