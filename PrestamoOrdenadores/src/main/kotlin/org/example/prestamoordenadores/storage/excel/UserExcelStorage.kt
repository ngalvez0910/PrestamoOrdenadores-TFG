package org.example.prestamoordenadores.storage.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class UserExcelStorage(
    private val userRepository: UserRepository,
) {
    fun generateExcel(): ByteArray {
        val usuarios = userRepository.findAll()

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Usuarios")

        val headerRow: Row = sheet.createRow(0)
        val headers = arrayOf("Número de identificación", "Email", "Nombre", "Apellidos", "Curso", "Tutor", "Rol", "Activo")
        headers.forEachIndexed { index, header ->
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }

        usuarios.forEachIndexed { rowIndex, user ->
            val row: Row = sheet.createRow(rowIndex + 1)

            row.createCell(0).setCellValue(user.numeroIdentificacion)
            row.createCell(1).setCellValue(user.email)
            row.createCell(2).setCellValue(user.nombre)
            row.createCell(3).setCellValue(user.apellidos)
            row.createCell(4).setCellValue(user.curso ?: "")
            row.createCell(5).setCellValue(user.tutor ?: "")
            row.createCell(6).setCellValue(user.rol.name)
            row.createCell(7).setCellValue(user.isActivo)
        }

        sheet.setColumnWidth(0, 25 * 256)
        sheet.setColumnWidth(1, 45 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 20 * 256)
        sheet.setColumnWidth(4, 20 * 256)
        sheet.setColumnWidth(5, 20 * 256)
        sheet.setColumnWidth(6, 15 * 256)
        sheet.setColumnWidth(7, 15 * 256)

        val outputStream = ByteArrayOutputStream()
        workbook.use {
            it.write(outputStream)
        }

        return outputStream.toByteArray()
    }
}