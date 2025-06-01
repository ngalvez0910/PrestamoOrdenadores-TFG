package org.example.prestamoordenadores.storage.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString // Re-use your existing utility
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

/**
 * Servicio para la generación de archivos Excel con datos de incidencias.
 *
 * Esta clase se encarga de recuperar la información de las incidencias desde el repositorio
 * y generar un archivo Excel con estos datos. El archivo contendrá una hoja llamada "Incidencias"
 * con columnas para el GUID, asunto, estado, usuario asociado y fecha de creación de la incidencia.
 *
 * @property incidenciaRepository Repositorio para acceder a los datos de las incidencias.
 * @author Natalia González Álvarez
 */
@Service
class IncidenciaExcelStorage(
    private val incidenciaRepository: IncidenciaRepository,
) {
    /**
     * Genera un archivo Excel que contiene todas las incidencias.
     *
     * Los datos de las incidencias se obtienen del [IncidenciaRepository]. El archivo Excel
     * tendrá una hoja con el nombre "Incidencias" y las siguientes columnas:
     * - "Guid": Identificador único de la incidencia.
     * - "Asunto": Asunto o título de la incidencia.
     * - "Estado": Estado actual de la incidencia (por ejemplo, "Abierta", "En Proceso", "Cerrada").
     * - "Usuario": Número de identificación del usuario que reportó la incidencia.
     * - "Fecha de Creación": Fecha y hora de creación de la incidencia, formateada.
     *
     * @return Un [ByteArray] que representa el contenido del archivo Excel generado.
     */
    fun generateExcel(): ByteArray {
        val incidencias = incidenciaRepository.findAll()

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Incidencias")

        val headerRow: Row = sheet.createRow(0)
        val headers = arrayOf("Guid", "Asunto", "Estado", "Usuario", "Fecha de Creación")
        headers.forEachIndexed { index, header ->
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }

        incidencias.forEachIndexed { rowIndex, incidencia ->
            val row: Row = sheet.createRow(rowIndex + 1)

            row.createCell(0).setCellValue(incidencia.guid)
            row.createCell(1).setCellValue(incidencia.asunto)
            row.createCell(2).setCellValue(incidencia.estadoIncidencia.name)
            row.createCell(3).setCellValue(incidencia.user.numeroIdentificacion)
            row.createCell(4).setCellValue(incidencia.createdDate.toDefaultDateTimeString())
        }

        sheet.setColumnWidth(0, 15 * 256)
        sheet.setColumnWidth(1, 25 * 256)
        sheet.setColumnWidth(2, 20 * 256)
        sheet.setColumnWidth(3, 15 * 256)
        sheet.setColumnWidth(4, 20 * 256)

        val outputStream = ByteArrayOutputStream()
        workbook.use {
            it.write(outputStream)
        }

        return outputStream.toByteArray()
    }
}