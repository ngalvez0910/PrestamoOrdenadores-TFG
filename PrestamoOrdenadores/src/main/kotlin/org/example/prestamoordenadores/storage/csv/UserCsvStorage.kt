package org.example.prestamoordenadores.storage.csv

import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

/**
 * Servicio para la generación y gestión de archivos CSV con datos de usuarios.
 *
 * Esta clase se encarga de recuperar la información de los **usuarios** desde el repositorio
 * y generar un archivo CSV con estos datos. También proporciona funcionalidades para guardar
 * los CSV generados en el sistema de archivos.
 *
 * @property userRepository Repositorio para acceder a los datos de los usuarios.
 * @author Natalia González Álvarez
 */
@Service
class UserCsvStorage(
    private val userRepository: UserRepository,
) {
    /**
     * Genera un archivo CSV que contiene todos los usuarios.
     *
     * Los datos de los usuarios se obtienen del [UserRepository]. El CSV
     * tendrá las siguientes columnas:
     * - "Guid": Identificador único del usuario.
     * - "Email": Dirección de correo electrónico del usuario.
     * - "Nombre": Nombre del usuario.
     * - "Apellidos": Apellidos del usuario.
     * - "Curso": Curso al que pertenece el usuario (puede estar vacío si no aplica).
     * - "Tutor": Tutor del usuario (puede estar vacío si no aplica).
     * - "Rol": Rol del usuario en el sistema (por ejemplo, "ADMIN", "USER").
     * - "Activo": Indica si la cuenta del usuario está activa (`true` o `false`).
     *
     * @return Un [ByteArray] que representa el contenido del archivo CSV generado.
     */
    fun generateCsv(): ByteArray {
        val usuarios = userRepository.findAll()
        val file = File.createTempFile("usuarios", ".csv")

        FileWriter(file, Charsets.UTF_8).use { writer ->
            val csvWriter = CSVWriterBuilder(writer)
                .withSeparator(';')
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()

            val header = arrayOf("Guid", "Email", "Nombre", "Apellidos", "Curso", "Tutor", "Rol", "Activo")
            csvWriter.writeNext(header, false)

            usuarios.forEach { user ->
                csvWriter.writeNext(
                    arrayOf(
                        user.guid,
                        user.email,
                        user.nombre,
                        user.apellidos,
                        user.curso ?: "",
                        user.tutor ?: "",
                        user.rol.name,
                        user.isActivo.toString()
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
     * Genera un CSV para todos los usuarios y lo guarda en un archivo.
     *
     * El nombre del archivo se construye utilizando el prefijo "usuarios_" seguido de la fecha actual
     * en formato "dd-MM-yyyy" y la extensión ".csv".
     */
    fun generateAndSaveCsv() {
        val csvData = generateCsv()
        val fileName = "usuarios_${LocalDate.now().toDefaultDateString()}.csv"
        saveCsvToFile(csvData, fileName)
    }
}