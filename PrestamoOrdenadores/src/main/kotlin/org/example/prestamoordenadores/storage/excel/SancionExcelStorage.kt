package org.example.prestamoordenadores.storage.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class SancionExcelStorage(
    private val sancionRepository: SancionRepository
) {
    fun generateExcel(): ByteArray {
        val sanciones = sancionRepository.findAll()

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sanciones")

        val headerRow: Row = sheet.createRow(0)
        val headers = arrayOf("Guid", "Usuario", "Tipo de Sanción", "Fecha de Sanción")
        headers.forEachIndexed { index, header ->
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }

        sanciones.forEachIndexed { rowIndex, sancion ->
            val row: Row = sheet.createRow(rowIndex + 1)

            row.createCell(0).setCellValue(sancion.guid)
            row.createCell(1).setCellValue(sancion.user.numeroIdentificacion)
            row.createCell(2).setCellValue(sancion.tipoSancion.name)
            row.createCell(3).setCellValue(sancion.fechaSancion.toDefaultDateString())
        }

        sheet.setColumnWidth(0, 15 * 256)
        sheet.setColumnWidth(1, 25 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 15 * 256)

        val outputStream = ByteArrayOutputStream()
        workbook.use {
            it.write(outputStream)
        }

        return outputStream.toByteArray()
    }
}