package org.example.prestamoordenadores.storage.pdf

import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@Service
class IncidenciaPdfStorage(
    private val incidenciaRepository: IncidenciaRepository,
    private val userRepository: UserRepository,
    private val dispositivoRepository: DispositivoRepository
) {

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
        val fileName = "incidencia_${LocalDate.now().toDefaultDateString()}.pdf"
        savePdfToFile(pdfData, fileName)
    }
}