package org.example.prestamoordenadores.storage

import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
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

@Service
class PrestamoPdfStorage(
    private val prestamoRepository: PrestamoRepository,
    private val userRepository: UserRepository,
    private val dispositivoRepository: DispositivoRepository
) {

    fun generatePdf(guid: String): ByteArray {
        val prestamo = prestamoRepository.findByGuid(guid)

        val outputStream = ByteArrayOutputStream()
        val pdfDoc = PdfDocument(PdfWriter(outputStream))

        val document = Document(pdfDoc)

        val loanTechText = Paragraph("LoanTech")
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setFontSize(36f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
            .setMarginBottom(10f)

        document.add(loanTechText)

        document.add(Paragraph("Solicitud de Préstamo")
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setFontSize(18f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER))

        document.add(Paragraph("Fecha de Solicitud: ${LocalDate.now().toDefaultDateString()}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT)
        )

        document.add(Paragraph("\n"))

        val user = userRepository.findByGuid(prestamo?.userGuid ?: "")

        document.add(Paragraph("Datos del usuario: ")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setBold()
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("Nº de Identificación: ${user?.numeroIdentificacion}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("Nombre: ${user?.nombre} ${user?.apellidos}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("Curso: ${user?.curso}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("Email: ${user?.email}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("\n"))

        val dispositivo = dispositivoRepository.findDispositivoByGuid(prestamo?.dispositivoGuid ?: "")

        document.add(Paragraph("Datos del dispositivo: ")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setBold()
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("Nº de Serie: ${dispositivo?.numeroSerie}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("Componentes: ${dispositivo?.componentes}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
        )

        document.add(Paragraph("\n"))

        document.add(Paragraph("Para recoger su dispositivo acerquese al departamento de informática y presente este documento.")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(9f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
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


    fun savePdfToFile(pdfData: ByteArray, fileName: String) {
        val dataFolderPath = Paths.get("data")
        if (!Files.exists(dataFolderPath)) {
            Files.createDirectories(dataFolderPath)
        }

        val filePath = dataFolderPath.resolve(fileName)
        val file = filePath.toFile()
        file.writeBytes(pdfData)
    }

    fun generateAndSavePdf(guid: String) {
        val pdfData = generatePdf(guid)
        val fileName = "prestamo_${LocalDate.now().toDefaultDateString()}.pdf"
        savePdfToFile(pdfData, fileName)
    }
}