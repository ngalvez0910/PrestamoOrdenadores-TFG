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
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

/**
 * Servicio para la generación y gestión de documentos PDF relacionados con préstamos.
 *
 * Esta clase se encarga de crear recibos o solicitudes de préstamo en formato PDF,
 * extrayendo la información relevante de los repositorios de préstamos, usuarios y dispositivos.
 * También proporciona funcionalidades para guardar los PDFs generados en el sistema de archivos.
 *
 * @property prestamoRepository Repositorio para acceder a los datos de préstamos.
 * @property userRepository Repositorio para acceder a los datos de usuarios.
 * @property dispositivoRepository Repositorio para acceder a los datos de dispositivos.
 * @author Natalia González Álvarez
 */
@Service
class PrestamoPdfStorage(
    private val prestamoRepository: PrestamoRepository,
    private val userRepository: UserRepository,
    private val dispositivoRepository: DispositivoRepository
) {

    private val PRIMARY_COLOR = DeviceRgb(52, 152, 219)
    private val TEXT_DARK_COLOR = DeviceRgb(50, 50, 50)
    private val BACKGROUND_MAIN_COLOR = DeviceRgb(245, 247, 250)
    private val NEUTRAL_MEDIUM_COLOR = DeviceRgb(200, 200, 200)

    /**
     * Genera un documento PDF con los detalles de un préstamo específico.
     *
     * El PDF incluye información del usuario que realiza el préstamo (número de identificación,
     * nombre completo, curso, email) y detalles del dispositivo prestado (número de serie, componentes).
     * También incluye un espacio para la fecha de entrega y la firma.
     *
     * @param guid El GUID del préstamo para el cual se generará el PDF.
     * @return Un [ByteArray] que contiene los datos del PDF generado.
     */
    fun generatePdf(guid: String): ByteArray {
        val prestamo = prestamoRepository.findByGuid(guid)

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
                Paragraph("Solicitud de Préstamo")
                    .setFontColor(PRIMARY_COLOR)
                    .setBold()
                    .setFontSize(18f)
                    .setTextAlignment(TextAlignment.RIGHT)
            ).setBorder(Border.NO_BORDER)
        )
        mainCard.addCell(Cell().add(headerTable).setBorder(Border.NO_BORDER))

        mainCard.addCell(
            Cell().add(
                Paragraph("Fecha de Solicitud: ${LocalDate.now().toDefaultDateString()}")
                    .setFontColor(TEXT_DARK_COLOR)
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20f)
            ).setBorder(Border.NO_BORDER)
        )

        val user = userRepository.findByGuid(prestamo?.user?.guid ?: "")
        val dispositivo = dispositivoRepository.findDispositivoByGuid(prestamo?.dispositivo?.guid ?: "")

        mainCard.addCell(
            createSectionTitle("Datos del usuario:")
        )
        mainCard.addCell(
            createReadOnlyField("Nº de Identificación:", user?.numeroIdentificacion)
        )
        mainCard.addCell(
            createReadOnlyField("Nombre:", "${user?.nombre} ${user?.apellidos}")
        )
        mainCard.addCell(
            createReadOnlyField("Curso:", user?.curso ?: " - ")
        )
        mainCard.addCell(
            createReadOnlyField("Email:", user?.email)
        )


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
            Cell().add(
                Paragraph("Para recoger su dispositivo acérquese al departamento de informática y presente este documento.")
                    .setFontColor(TEXT_DARK_COLOR)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(20f)
            ).setBorder(Border.NO_BORDER)
        )

        val signatureTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .useAllAvailableWidth()
            .setBorderTop(SolidBorder(NEUTRAL_MEDIUM_COLOR, 1f))
            .setPaddingTop(20f)
            .setMarginTop(20f)

        signatureTable.addCell(Cell().add(Paragraph("Fecha de Entrega:").setBold().setFontColor(TEXT_DARK_COLOR)).setBorder(Border.NO_BORDER))
        signatureTable.addCell(Cell().add(Paragraph("Firma:").setBold().setFontColor(TEXT_DARK_COLOR)).setBorder(Border.NO_BORDER))

        signatureTable.addCell(Cell().add(Paragraph("").setHeight(20f)).setBorder(Border.NO_BORDER))
        signatureTable.addCell(Cell().add(Paragraph("______________________").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER))

        mainCard.addCell(Cell().add(signatureTable).setBorder(Border.NO_BORDER))

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
     * Genera un PDF para un préstamo específico y lo guarda en un archivo.
     *
     * El nombre del archivo se construye utilizando el prefijo "prestamo_" seguido de la fecha actual
     * en formato "dd-MM-yyyy" y la extensión ".pdf".
     *
     * @param guid El GUID del préstamo para el cual se generará y guardará el PDF.
     */
    fun generateAndSavePdf(guid: String) {
        val pdfData = generatePdf(guid)
        val fileName = "prestamo_${LocalDate.now().toDefaultDateString()}.pdf"
        savePdfToFile(pdfData, fileName)
    }
}