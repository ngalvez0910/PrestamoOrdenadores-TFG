package org.example.prestamoordenadores.storage.csv

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

/**
 * Servicio para la generación y gestión de archivos CSV con datos de dispositivos.
 *
 * Esta clase se encarga de recuperar la información de los dispositivos desde el repositorio
 * y generar un archivo CSV con estos datos. También proporciona funcionalidades para guardar
 * los CSV generados en el sistema de archivos.
 *
 * @property dispositivoRepository Repositorio para acceder a los datos de los dispositivos.
 * @author Natalia González Álvarez
 */
@Service
class DispositivoCsvStorage(
    private val dispositivoRepository: DispositivoRepository,
) {
    /**
     * Genera un archivo CSV que contiene todos los dispositivos.
     *
     * Los datos de los dispositivos se obtienen del [DispositivoRepository]. El CSV
     * tendrá las siguientes columnas:
     * - "Guid": Identificador único del dispositivo.
     * - "Nº Serie": Número de serie del dispositivo.
     * - "Componentes": Componentes del dispositivo.
     * - "Estado": Estado actual del dispositivo (por ejemplo, disponible, en reparación).
     * - "Incidencias": GUID de la incidencia asociada, si la hay.
     * - "Borrado": Estado de borrado lógico del dispositivo.
     *
     * @return Un [ByteArray] que representa el contenido del archivo CSV generado.
     */
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

            val header = arrayOf("Guid", "Nº Serie", "Componentes", "Estado", "Incidencias", "Borrado")
            csvWriter.writeNext(header, false)

            dispositivos.forEach { dispositivo ->
                csvWriter.writeNext(
                    arrayOf(
                        dispositivo.guid,
                        dispositivo.numeroSerie,
                        dispositivo.componentes,
                        dispositivo.estadoDispositivo.name,
                        dispositivo.incidencia?.guid ?:"",
                        dispositivo.isDeleted.toString()
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
     * Genera un CSV para todos los dispositivos y lo guarda en un archivo.
     *
     * El nombre del archivo se construye utilizando el prefijo "dispositivos_" seguido de la fecha actual
     * en formato "dd-MM-yyyy" y la extensión ".csv".
     */
    fun generateAndSaveCsv() {
        val csvData = generateCsv()
        val fileName = "dispositivos_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}