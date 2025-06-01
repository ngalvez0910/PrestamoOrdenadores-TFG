package org.example.prestamoordenadores.storage.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

/**
 * Servicio para la generación de archivos Excel con datos de sanciones.
 *
 * Esta clase se encarga de recuperar la información de las **sanciones** desde el repositorio
 * y generar un archivo Excel con estos datos. El archivo contendrá una hoja llamada "Sanciones"
 * con columnas para el GUID, el usuario sancionado, el tipo de sanción y la fecha de la sanción.
 *
 * @property sancionRepository Repositorio para acceder a los datos de las sanciones.
 * @author Natalia González Álvarez
 */
@Service
class SancionExcelStorage(
    private val sancionRepository: SancionRepository
) {
    /**
     * Genera un archivo Excel que contiene todas las sanciones.
     *
     * Los datos de las sanciones se obtienen del [SancionRepository]. El archivo Excel
     * tendrá una hoja con el nombre "Sanciones" y las siguientes columnas:
     * - "Guid": Identificador único de la sanción.
     * - "Usuario": Número de identificación del usuario sancionado.
     * - "Tipo de Sanción": El tipo de sanción aplicada (por ejemplo, "Leve", "Grave").
     * - "Fecha de Sanción": La fecha en que se aplicó la sanción, formateada.
     *
     * @return Un [ByteArray] que representa el contenido del archivo Excel generado.
     */
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