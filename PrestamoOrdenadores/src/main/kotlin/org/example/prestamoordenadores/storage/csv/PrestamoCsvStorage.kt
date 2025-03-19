package org.example.prestamoordenadores.storage.csv

import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@Service
class PrestamoCsvStorage(
    private val prestamoRepository: PrestamoRepository,
) {
    fun generateCsv(): ByteArray {
        val prestamos = prestamoRepository.findAll()
        val file = File.createTempFile("prestamos", ".csv")

        FileWriter(file, Charsets.UTF_8).use { writer ->
            val csvWriter = CSVWriterBuilder(writer)
                .withSeparator(';')
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()

            val header = arrayOf("Guid", "Usuario", "Dispositivo", "Estado", "Fecha Préstamo", "Fecha Devolución")
            csvWriter.writeNext(header, false)

            prestamos.forEach { prestamo ->
                csvWriter.writeNext(
                    arrayOf(
                        prestamo.guid,
                        prestamo.userGuid,
                        prestamo.dispositivoGuid,
                        prestamo.estadoPrestamo.name,
                        prestamo.fechaPrestamo.toDefaultDateString(),
                        prestamo.fechaDevolucion.toDefaultDateString()
                    ),
                    false
                )
            }

            csvWriter.close()
        }

        return file.readBytes()
    }

    fun saveCsvToFile(csvData: ByteArray, fileName: String) {
        val dataFolderPath = Paths.get("data")
        if (!Files.exists(dataFolderPath)) {
            Files.createDirectories(dataFolderPath)
        }

        val filePath = dataFolderPath.resolve(fileName)
        File(filePath.toUri()).writeBytes(csvData)
    }

    fun generateAndSaveCsv() {
        val csvData = generateCsv()
        val fileName = "prestamos_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}