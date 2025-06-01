package org.example.prestamoordenadores.storage.excel

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.springframework.stereotype.Service

/**
 * Servicio para la generación de archivos Excel con datos de dispositivos.
 *
 * Esta clase se encarga de recuperar la información de los dispositivos desde el repositorio
 * y generar un archivo Excel con estos datos. El archivo contendrá una hoja llamada "Dispositivos"
 * con columnas para el GUID, número de serie, componentes, estado, GUID de incidencia asociada
 * y estado de borrado del dispositivo.
 *
 * @property dispositivoRepository Repositorio para acceder a los datos de los dispositivos.
 * @author Natalia González Álvarez
 */
@Service
class DispositivoExcelStorage(
    private val dispositivoRepository: DispositivoRepository,
) {
    /**
     * Genera un archivo Excel que contiene todos los dispositivos.
     *
     * Los datos de los dispositivos se obtienen del [DispositivoRepository]. El archivo Excel
     * tendrá una hoja con el nombre "Dispositivos" y las siguientes columnas:
     * - "Guid": Identificador único del dispositivo.
     * - "Nº Serie": Número de serie del dispositivo.
     * - "Componentes": Componentes del dispositivo.
     * - "Estado": Estado actual del dispositivo (por ejemplo, disponible, en reparación).
     * - "Incidencias": GUID de la incidencia asociada, si la hay.
     * - "Borrado": Estado de borrado lógico del dispositivo.
     *
     * @return Un [ByteArray] que representa el contenido del archivo Excel generado.
     */
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