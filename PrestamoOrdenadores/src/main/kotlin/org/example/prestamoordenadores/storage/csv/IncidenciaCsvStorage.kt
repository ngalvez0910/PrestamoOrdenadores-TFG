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

/**
 * Servicio para la generación y gestión de archivos CSV con datos de incidencias.
 *
 * Esta clase se encarga de recuperar la información de las **incidencias** desde el repositorio
 * y generar un archivo CSV con estos datos. También proporciona funcionalidades para guardar
 * los CSV generados en el sistema de archivos.
 *
 * @property incidenciaRepository Repositorio para acceder a los datos de las incidencias.
 * @author Natalia González Álvarez
 */
@Service
class IncidenciaCsvStorage(
    private val incidenciaRepository: IncidenciaRepository,
) {
    /**
     * Genera un archivo CSV que contiene todas las incidencias.
     *
     * Los datos de las incidencias se obtienen del [IncidenciaRepository]. El CSV
     * tendrá las siguientes columnas:
     * - "Guid": Identificador único de la incidencia.
     * - "Asunto": Asunto o título de la incidencia.
     * - "Estado": Estado actual de la incidencia (por ejemplo, "Abierta", "En Proceso", "Cerrada").
     * - "Usuario": GUID del usuario que reportó la incidencia.
     * - "Fecha": Fecha y hora de creación de la incidencia, formateada.
     *
     * @return Un [ByteArray] que representa el contenido del archivo CSV generado.
     */
    fun generateCsv(): ByteArray {
        val incidencias = incidenciaRepository.findAll() // Changed 'usuarios' to 'incidencias' for clarity
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

            incidencias.forEach { incidencia -> // Changed 'usuarios.forEach' to 'incidencias.forEach' for clarity
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

    /**
     * Guarda los datos de un CSV en un archivo en el directorio "data".
     * Si el directorio "data" no existe, se crea automáticamente.
     *
     * @param csvData Los bytes que conforman el contenido del CSV.
     * @param fileName El nombre del archivo CSV a guardar.
     */
    fun saveCsvToFile(csvData: ByteArray, fileName: String) {
        val dataFolderPath = Paths.get("data")
        if (!Files.exists(dataFolderPath)) {
            Files.createDirectories(dataFolderPath)
        }

        val filePath = dataFolderPath.resolve(fileName)
        Files.write(filePath, csvData)
    }

    /**
     * Genera un CSV para todas las incidencias y lo guarda en un archivo.
     *
     * El nombre del archivo se construye utilizando el prefijo "incidencias_" seguido de la fecha actual
     * en formato "dd-MM-yyyy" y la extensión ".csv".
     */
    fun generateAndSaveCsv() {
        val csvData = generateCsv()
        val fileName = "incidencias_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}