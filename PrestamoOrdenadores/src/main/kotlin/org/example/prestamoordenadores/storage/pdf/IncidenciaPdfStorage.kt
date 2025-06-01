package org.example.prestamoordenadores.storage.pdf

import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

/**
 * Servicio para la generación y gestión de documentos PDF relacionados con incidencias.
 *
 * Esta clase se encarga de crear reportes PDF de incidencias, extrayendo la información
 * relevante de los repositorios de incidencias y dispositivos. También proporciona
 * funcionalidades para guardar los PDFs generados en el sistema de archivos.
 *
 * @property incidenciaRepository Repositorio para acceder a los datos de incidencias.
 * @property dispositivoRepository Repositorio para acceder a los datos de dispositivos.
 * @author Natalia González Álvarez
 */
@Service
class IncidenciaPdfStorage(
    private val incidenciaRepository: IncidenciaRepository,
    private val dispositivoRepository: DispositivoRepository
) {

    /**
     * Genera un documento PDF con los detalles de una incidencia específica.
     *
     * El PDF incluye información como el número de serie y componentes del dispositivo asociado,
     * así como el asunto y la descripción de la incidencia.
     *
     * @param guid El GUID de la incidencia para la cual se generará el PDF.
     * @return Un [ByteArray] que contiene los datos del PDF generado.
     */
    fun generatePdf(guid: String): ByteArray {
        val incidencia = incidenciaRepository.findIncidenciaByGuid(guid)

        val outputStream = ByteArrayOutputStream()
        val pdfDoc = PdfDocument(PdfWriter(outputStream))

        val document = Document(pdfDoc)

        val loanTechText = Paragraph("LoanTech")
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setFontSize(36f)
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(10f)

        document.add(loanTechText)

        document.add(
            Paragraph("Reporte de Incidencia")
                .setFontColor(ColorConstants.BLACK)
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER))

        document.add(
            Paragraph("Fecha de Reporte: ${LocalDate.now().toDefaultDateString()}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        document.add(Paragraph("\n"))

        val dispositivo = dispositivoRepository.findDispositivoByIncidenciaGuid(guid)

        document.add(
            Paragraph("Datos del dispositivo: ")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Nº de Serie: ${dispositivo?.numeroSerie}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Componentes: ${dispositivo?.componentes}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Datos de la incidencia: ")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Asunto: ${incidencia?.asunto}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Descripción: ${incidencia?.descripcion}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(Paragraph("\n"))

        document.close()

        return outputStream.toByteArray()
    }

    /**
     * Guarda los datos de un PDF en un archivo en el directorio "data".
     * Si el directorio "data" no existe, se crea automáticamente.
     *
     * @param pdfData Los bytes que conforman el contenido del PDF.
     * @param fileName El nombre del archivo PDF a guardar.
     */
    fun savePdfToFile(pdfData: ByteArray, fileName: String) {
        val dataFolderPath = Paths.get("data")
        if (!Files.exists(dataFolderPath)) {
            Files.createDirectories(dataFolderPath)
        }

        val filePath = dataFolderPath.resolve(fileName)
        val file = filePath.toFile()
        file.writeBytes(pdfData)
    }

    /**
     * Genera un PDF para una incidencia específica y lo guarda en un archivo.
     *
     * El nombre del archivo se construye utilizando el prefijo "incidencia_" seguido de la fecha actual
     * en formato "dd-MM-yyyy" y la extensión ".pdf".
     *
     * @param guid El GUID de la incidencia para la cual se generará y guardará el PDF.
     */
    fun generateAndSavePdf(guid: String) {
        val pdfData = generatePdf(guid)
        val fileName = "incidencia_${LocalDate.now().toDefaultDateString()}.pdf"
        savePdfToFile(pdfData, fileName)
    }
}