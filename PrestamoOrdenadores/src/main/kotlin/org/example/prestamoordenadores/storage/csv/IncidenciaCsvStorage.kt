package org.example.prestamoordenadores.storage.csv

import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@Service
class IncidenciaCsvStorage(
    private val incidenciaRepository: IncidenciaRepository,
) {
    fun generateCsv(): ByteArray {
        val usuarios = incidenciaRepository.findAll()
        val file = File.createTempFile("incidencias", ".csv")

        FileWriter(file, Charsets.UTF_8).use { writer ->
            val csvWriter = CSVWriterBuilder(writer)
                .withSeparator(';')
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()

            val header = arrayOf("Guid", "Asunto", "Estado", "Usuario", "Fecha")
            csvWriter.writeNext(header, false)

            usuarios.forEach { incidencia ->
                csvWriter.writeNext(
                    arrayOf(
                        incidencia.guid,
                        incidencia.asunto,
                        incidencia.estadoIncidencia.name,
                        incidencia.user.guid,
                        incidencia.createdDate.toDefaultDateTimeString()
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
        val fileName = "incidencias_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}