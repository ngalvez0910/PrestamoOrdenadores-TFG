package org.example.prestamoordenadores.storage

import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.property.UnitValue
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@Service
class DispositivoPdfStorage(
    private val dispositivoRepository: DispositivoRepository,
) {

    fun generatePdf(): ByteArray {
        val dispositivos = dispositivoRepository.findAll()

        val outputStream = ByteArrayOutputStream()
        val pdfDoc = PdfDocument(PdfWriter(outputStream))

        pdfDoc.defaultPageSize = PageSize.A4.rotate()
        val document = Document(pdfDoc)

        val loanTechText = Paragraph("LoanTech")
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setFontSize(36f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
            .setMarginBottom(10f)

        document.add(loanTechText)

        document.add(Paragraph("Reporte de Dispositivos")
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setFontSize(18f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER))

        document.add(Paragraph("Fecha de Generación: ${LocalDate.now().toDefaultDateString()}")
            .setFontColor(ColorConstants.BLACK)
            .setFontSize(12f)
            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.RIGHT)
        )

        document.add(Paragraph("\n"))

        val table = Table(6)

        table.addCell(Cell().add(Paragraph("Guid")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
        table.addCell(Cell().add(Paragraph("Nº Serie")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
        table.addCell(Cell().add(Paragraph("Componentes")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
        table.addCell(Cell().add(Paragraph("Estado")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
        table.addCell(Cell().add(Paragraph("Incidencias")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
        table.addCell(Cell().add(Paragraph("Stock")).setBackgroundColor(ColorConstants.LIGHT_GRAY))

        dispositivos.forEach { dispositivo ->
            table.addCell(Cell().add(Paragraph(dispositivo.guid)))
            table.addCell(Cell().add(Paragraph(dispositivo.numeroSerie)))
            table.addCell(Cell().add(Paragraph(dispositivo.componentes)))

            val estadoColor = when (dispositivo.estadoDispositivo) {
                EstadoDispositivo.DISPONIBLE -> ColorConstants.GREEN
                EstadoDispositivo.NO_DISPONIBLE -> ColorConstants.RED
                EstadoDispositivo.PRESTADO -> ColorConstants.MAGENTA
                else -> ColorConstants.BLACK
            }
            table.addCell(Cell().add(Paragraph(dispositivo.estadoDispositivo.name).setFontColor(estadoColor)))

            table.addCell(Cell().add(Paragraph(dispositivo.incidenciaGuid ?: "")))
            table.addCell(Cell().add(Paragraph(dispositivo.stock.toString())))
        }

        table.setWidth(UnitValue.createPercentValue(100f))
        table.setFixedLayout()

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
}