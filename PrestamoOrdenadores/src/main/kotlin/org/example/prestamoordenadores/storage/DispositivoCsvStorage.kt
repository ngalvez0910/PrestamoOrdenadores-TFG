package org.example.prestamoordenadores.storage

import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@Service
class DispositivoCsvStorage(
    private val dispositivoRepository: DispositivoRepository,
) {
    fun generateCsv(): ByteArray {
        val dispositivos = dispositivoRepository.findAll()
        val file = File.createTempFile("dispositivos", ".csv")

        FileWriter(file, Charsets.UTF_8).use { writer ->
            val csvWriter = CSVWriterBuilder(writer)
                .withSeparator(';')
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()

            val header = arrayOf("Guid", "Nº Serie", "Componentes", "Estado", "Incidencias", "Stock", "Activo")
            csvWriter.writeNext(header, false)

            dispositivos.forEach { dispositivo ->
                csvWriter.writeNext(
                    arrayOf(
                        dispositivo.guid,
                        dispositivo.numeroSerie,
                        dispositivo.componentes,
                        dispositivo.estadoDispositivo.name,
                        dispositivo.incidenciaGuid ?: "",
                        dispositivo.stock.toString(),
                        dispositivo.isActivo.toString()
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
        val fileName = "dispositivos_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}
