package org.example.prestamoordenadores.storage.excel

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.springframework.stereotype.Service

@Service
class DispositivoExcelStorage(
    private val dispositivoRepository: DispositivoRepository,
) {
    fun generateExcel(): ByteArray {
        val dispositivos = dispositivoRepository.findAll()

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Dispositivos")

        val headerRow: Row = sheet.createRow(0)
        val headers = arrayOf("Guid", "Nº Serie", "Componentes", "Estado", "Incidencias", "Borrado")
        headers.forEachIndexed { index, header ->
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }

        dispositivos.forEachIndexed { rowIndex, dispositivo ->
            val row: Row = sheet.createRow(rowIndex + 1)

            row.createCell(0).setCellValue(dispositivo.guid)
            row.createCell(1).setCellValue(dispositivo.numeroSerie)
            row.createCell(2).setCellValue(dispositivo.componentes)
            row.createCell(3).setCellValue(dispositivo.estadoDispositivo.name)
            row.createCell(4).setCellValue(dispositivo.incidencia?.guid ?: "")
            row.createCell(5).setCellValue(dispositivo.isDeleted)
        }

        sheet.setColumnWidth(0, 15 * 256)
        sheet.setColumnWidth(1, 25 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 20 * 256)
        sheet.setColumnWidth(4, 20 * 256)
        sheet.setColumnWidth(5, 20 * 256)

        val outputStream = ByteArrayOutputStream()
        workbook.use {
            it.write(outputStream)
        }

        return outputStream.toByteArray()
    }
}