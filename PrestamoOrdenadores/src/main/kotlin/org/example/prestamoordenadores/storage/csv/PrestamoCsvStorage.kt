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

/**
 * Servicio para la generación y gestión de archivos CSV con datos de préstamos.
 *
 * Esta clase se encarga de recuperar la información de los **préstamos** desde el repositorio
 * y generar un archivo CSV con estos datos. También proporciona funcionalidades para guardar
 * los CSV generados en el sistema de archivos.
 *
 * @property prestamoRepository Repositorio para acceder a los datos de los préstamos.
 * @author Natalia González Álvarez
 */
@Service
class PrestamoCsvStorage(
    private val prestamoRepository: PrestamoRepository,
) {
    /**
     * Genera un archivo CSV que contiene todos los préstamos.
     *
     * Los datos de los préstamos se obtienen del [PrestamoRepository]. El CSV
     * tendrá las siguientes columnas:
     * - "Guid": Identificador único del préstamo.
     * - "Usuario": GUID del usuario asociado al préstamo.
     * - "Dispositivo": GUID del dispositivo asociado al préstamo.
     * - "Estado": Estado actual del préstamo (por ejemplo, "Pendiente", "Activo", "Devuelto").
     * - "Fecha Préstamo": Fecha en que se realizó el préstamo, formateada como cadena de texto.
     * - "Fecha Devolución": Fecha de devolución del dispositivo, formateada como cadena de texto.
     *
     * @return Un [ByteArray] que representa el contenido del archivo CSV generado.
     */
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
                        prestamo.user.guid,
                        prestamo.dispositivo.guid,
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
     * Genera un CSV para todos los préstamos y lo guarda en un archivo.
     *
     * El nombre del archivo se construye utilizando el prefijo "prestamos_" seguido de la fecha actual
     * en formato "dd-MM-yyyy" y la extensión ".csv".
     */
    fun generateAndSaveCsv() {
        val csvData = generateCsv()
        val fileName = "prestamos_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}