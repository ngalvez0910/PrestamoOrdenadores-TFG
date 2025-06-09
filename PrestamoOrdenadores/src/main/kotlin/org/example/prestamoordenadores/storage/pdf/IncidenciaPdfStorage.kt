package org.example.prestamoordenadores.storage.pdf

import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.itextpdf.layout.property.VerticalAlignment
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

    private val PRIMARY_COLOR = DeviceRgb(52, 152, 219)
    private val TEXT_DARK_COLOR = DeviceRgb(50, 50, 50)
    private val BACKGROUND_MAIN_COLOR = DeviceRgb(245, 247, 250)
    private val NEUTRAL_MEDIUM_COLOR = DeviceRgb(200, 200, 200)

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
        document.setMargins(40f, 30f, 40f, 30f)

        val mainCard = Table(UnitValue.createPercentArray(1))
            .useAllAvailableWidth()
            .setBackgroundColor(ColorConstants.WHITE)
            .setBorder(SolidBorder(NEUTRAL_MEDIUM_COLOR, 1f))
            .setMarginBottom(20f)

        val headerTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .useAllAvailableWidth()
            .setBorderBottom(SolidBorder(NEUTRAL_MEDIUM_COLOR, 1f))
            .setPaddingBottom(15f)
            .setMarginBottom(10f)

        headerTable.addCell(
            Cell().add(
                Paragraph("LoanTech")
                    .setFontColor(PRIMARY_COLOR)
                    .setBold()
                    .setFontSize(36f)
                    .setTextAlignment(TextAlignment.LEFT)
            ).setBorder(Border.NO_BORDER)
        )

        headerTable.addCell(
            Cell().add(
                Paragraph("Reporte de Incidencia")
                    .setFontColor(PRIMARY_COLOR)
                    .setBold()
                    .setFontSize(18f)
                    .setTextAlignment(TextAlignment.RIGHT)
            ).setBorder(Border.NO_BORDER)
        )
        mainCard.addCell(Cell().add(headerTable).setBorder(Border.NO_BORDER))


        mainCard.addCell(
            Cell().add(
                Paragraph("Fecha de Reporte: ${LocalDate.now().toDefaultDateString()}")
                    .setFontColor(TEXT_DARK_COLOR)
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20f)
            ).setBorder(Border.NO_BORDER)
        )

        val dispositivo = dispositivoRepository.findDispositivoByIncidenciaGuid(guid)

        mainCard.addCell(
            createSectionTitle("Datos del dispositivo:")
        )
        mainCard.addCell(
            createReadOnlyField("Nº de Serie:", dispositivo?.numeroSerie)
        )
        mainCard.addCell(
            createReadOnlyField("Componentes:", dispositivo?.componentes)
        )

        mainCard.addCell(
            createSectionTitle("Datos de la incidencia:")
        )
        mainCard.addCell(
            createReadOnlyField("Asunto:", incidencia?.asunto)
        )
        mainCard.addCell(
            createReadOnlyField("Descripción:", incidencia?.descripcion)
        )

        document.add(mainCard)
        document.close()

        return outputStream.toByteArray()
    }

    private fun createSectionTitle(title: String): Cell {
        return Cell().add(
            Paragraph(title)
                .setFontColor(TEXT_DARK_COLOR)
                .setFontSize(12f)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10f)
        ).setBorder(Border.NO_BORDER)
    }

    private fun createReadOnlyField(label: String, value: String?): Cell {
        val table = Table(UnitValue.createPercentArray(1))
            .useAllAvailableWidth()
            .setMarginBottom(8f)

        table.addCell(
            Cell().add(
                Paragraph(label)
                    .setFontSize(9f)
                    .setFontColor(TEXT_DARK_COLOR)
                    .setBold()
                    .setOpacity(0.8f)
                    .setTextAlignment(TextAlignment.LEFT)
            ).setBorder(Border.NO_BORDER)
        )

        table.addCell(
            Cell().add(
                Paragraph(value ?: "N/A")
                    .setFontSize(11f)
                    .setFontColor(TEXT_DARK_COLOR)
                    .setTextAlignment(TextAlignment.LEFT)
            )
                .setBackgroundColor(BACKGROUND_MAIN_COLOR)
                .setBorder(SolidBorder(NEUTRAL_MEDIUM_COLOR, 1f))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
        )
        return Cell().add(table).setBorder(Border.NO_BORDER)
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