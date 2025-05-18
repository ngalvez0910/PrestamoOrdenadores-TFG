package org.example.prestamoordenadores.storage.csv

import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@Service
class SancionCsvStorage(
    private val sancionRepository: SancionRepository
) {
    fun generateCsv(): ByteArray {
        val sanciones = sancionRepository.findAll()
        val file = File.createTempFile("sanciones", ".csv")

        FileWriter(file, Charsets.UTF_8).use { writer ->
            val csvWriter = CSVWriterBuilder(writer)
                .withSeparator(';')
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()

            val header = arrayOf("Guid", "Usuario", "Tipo", "Fecha")
            csvWriter.writeNext(header, false)

            sanciones.forEach { sancion ->
                csvWriter.writeNext(
                    arrayOf(
                        sancion.guid,
                        sancion.user.guid,
                        sancion.tipoSancion.name,
                        sancion.fechaSancion.toDefaultDateString()
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
        Files.write(filePath, csvData)
    }

    fun generateAndSaveCsv() {
        val csvData = generateCsv()
        val fileName = "sanciones_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}
