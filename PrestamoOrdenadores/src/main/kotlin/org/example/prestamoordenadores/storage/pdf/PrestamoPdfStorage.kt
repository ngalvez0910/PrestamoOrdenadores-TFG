package org.example.prestamoordenadores.storage.pdf

import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
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

        val loanTechText = Paragraph("LoanTech")
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setFontSize(36f)
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(10f)

        document.add(loanTechText)

        document.add(
            Paragraph("Solicitud de Préstamo")
                .setFontColor(ColorConstants.BLACK)
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER))

        document.add(
            Paragraph("Fecha de Solicitud: ${LocalDate.now().toDefaultDateString()}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        document.add(Paragraph("\n"))

        val user = userRepository.findByGuid(prestamo?.user?.guid ?: "")

        document.add(
            Paragraph("Datos del usuario: ")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Nº de Identificación: ${user?.numeroIdentificacion}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Nombre: ${user?.nombre} ${user?.apellidos}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Curso: ${user?.curso ?: ""}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(
            Paragraph("Email: ${user?.email}")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(Paragraph("\n"))

        val dispositivo = dispositivoRepository.findDispositivoByGuid(prestamo?.dispositivo?.guid ?: "")

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

        document.add(Paragraph("\n"))

        document.add(
            Paragraph("Para recoger su dispositivo acerquese al departamento de informática y presente este documento.")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(9f)
                .setTextAlignment(TextAlignment.LEFT)
        )

        document.add(Paragraph("\n"))

        val table = Table(UnitValue.createPercentArray(floatArrayOf(2f, 3f)))
            .useAllAvailableWidth()

        table.addCell(Cell().add(Paragraph("Fecha de Entrega:").setBold()).setBorder(null))
        table.addCell(Cell().add(Paragraph("Firma:").setBold()).setBorder(null))

        table.addCell(Cell().add(Paragraph("")).setBorder(null))
        table.addCell(Cell().add(Paragraph("______________________")).setBorder(null))

        document.add(table)


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