package org.example.prestamoordenadores.storage.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class PrestamoExcelStorage(
    private val prestamoRepository: PrestamoRepository,
) {
    fun generateExcel(): ByteArray {
        val prestamos = prestamoRepository.findAll()

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Prestamos")

        val headerRow: Row = sheet.createRow(0)
        val headers = arrayOf("Guid", "Usuario", "Dispositivo", "Estado", "Fecha Préstamo", "Fecha Devolución")
        headers.forEachIndexed { index, header ->
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }

        prestamos.forEachIndexed { rowIndex, prestamo ->
            val row: Row = sheet.createRow(rowIndex + 1)

            row.createCell(0).setCellValue(prestamo.guid)
            row.createCell(1).setCellValue(prestamo.user.numeroIdentificacion)
            row.createCell(2).setCellValue(prestamo.dispositivo.numeroSerie)
            row.createCell(3).setCellValue(prestamo.estadoPrestamo.name)
            row.createCell(4).setCellValue(prestamo.fechaPrestamo.toDefaultDateString())
            row.createCell(5).setCellValue(prestamo.fechaDevolucion.toDefaultDateString())
        }

        sheet.setColumnWidth(0, 15 * 256)
        sheet.setColumnWidth(1, 15 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 15 * 256)
        sheet.setColumnWidth(4, 20 * 256)
        sheet.setColumnWidth(5, 20 * 256)

        val outputStream = ByteArrayOutputStream()
        workbook.use {
            it.write(outputStream)
        }

        return outputStream.toByteArray()
    }
}