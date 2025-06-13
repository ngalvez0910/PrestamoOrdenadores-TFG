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

/**
 * Servicio para la generación y gestión de archivos CSV con datos de sanciones.
 *
 * Esta clase se encarga de recuperar la información de las **sanciones** desde el repositorio
 * y generar un archivo CSV con estos datos. También proporciona funcionalidades para guardar
 * los CSV generados en el sistema de archivos.
 *
 * @property sancionRepository Repositorio para acceder a los datos de las sanciones.
 * @author Natalia González Álvarez
 */
@Service
class SancionCsvStorage(
    private val sancionRepository: SancionRepository
) {
    /**
     * Genera un archivo CSV que contiene todas las sanciones.
     *
     * Los datos de las sanciones se obtienen del [SancionRepository]. El CSV
     * tendrá las siguientes columnas:
     * - "Guid": Identificador único de la sanción.
     * - "Usuario": GUID del usuario sancionado.
     * - "Tipo": El tipo de sanción aplicada (por ejemplo, "Leve", "Grave").
     * - "Fecha": La fecha en que se aplicó la sanción, formateada como cadena de texto.
     *
     * @return Un [ByteArray] que representa el contenido del archivo CSV generado.
     */
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
     * Genera un CSV para todas las sanciones y lo guarda en un archivo.
     *
     * El nombre del archivo se construye utilizando el prefijo "sanciones_" seguido de la fecha actual
     * en formato "dd-MM-yyyy" y la extensión ".csv".
     */
    fun generateAndSaveCsv() {
        val csvData = generateCsv()
        val fileName = "sanciones_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}