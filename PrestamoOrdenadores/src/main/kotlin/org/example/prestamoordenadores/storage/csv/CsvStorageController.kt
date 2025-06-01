package org.example.prestamoordenadores.storage.csv

import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

/**
 * Controlador REST para la exportación de datos a archivos CSV.
 *
 * Este controlador proporciona endpoints para generar y descargar archivos CSV
 * que contienen datos de dispositivos, incidencias, préstamos, sanciones y usuarios.
 * Todas las operaciones en este controlador requieren que el usuario tenga el rol 'ADMIN'.
 *
 * @property dispositivoCsvStorage Servicio para generar CSV de dispositivos.
 * @property incidenciaCsvStorage Servicio para generar CSV de incidencias.
 * @property prestamoCsvStorage Servicio para generar CSV de préstamos.
 * @property sancionCsvStorage Servicio para generar CSV de sanciones.
 * @property userCsvStorage Servicio para generar CSV de usuarios.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/storage/csv")
@PreAuthorize("hasRole('ADMIN')")
class CsvStorageController
@Autowired constructor(
    private val dispositivoCsvStorage: DispositivoCsvStorage,
    private val incidenciaCsvStorage: IncidenciaCsvStorage,
    private val prestamoCsvStorage: PrestamoCsvStorage,
    private val sancionCsvStorage: SancionCsvStorage,
    private val userCsvStorage: UserCsvStorage
) {
    /**
     * Exporta los datos de los dispositivos a un archivo CSV y lo descarga.
     * El archivo se guarda temporalmente en el directorio "data".
     *
     * @return [ResponseEntity] con los bytes del archivo CSV y las cabeceras HTTP adecuadas para la descarga,
     * o un estado 404 si el archivo no se encuentra.
     */
    @GetMapping("/dispositivos")
    fun exportCsvDispositivos(): ResponseEntity<ByteArray> {
        dispositivoCsvStorage.generateAndSaveCsv()

        val fileName = "dispositivos_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        return if (Files.exists(filePath)) {
            val fileContent = Files.readAllBytes(filePath)
            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
                add(HttpHeaders.CONTENT_TYPE, "text/csv")
            }
            ResponseEntity(fileContent, headers, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Exporta los datos de las incidencias a un archivo CSV y lo descarga.
     * El archivo se guarda temporalmente en el directorio "data".
     *
     * @return [ResponseEntity] con los bytes del archivo CSV y las cabeceras HTTP adecuadas para la descarga,
     * o un estado 404 si el archivo no se encuentra.
     */
    @GetMapping("/incidencias")
    fun exportCsvIncidencias(): ResponseEntity<ByteArray> {
        incidenciaCsvStorage.generateAndSaveCsv()

        val fileName = "incidencias_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        return if (Files.exists(filePath)) {
            val fileContent = Files.readAllBytes(filePath)
            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
                add(HttpHeaders.CONTENT_TYPE, "text/csv")
            }
            ResponseEntity(fileContent, headers, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Exporta los datos de los préstamos a un archivo CSV y lo descarga.
     * El archivo se guarda temporalmente en el directorio "data".
     *
     * @return [ResponseEntity] con los bytes del archivo CSV y las cabeceras HTTP adecuadas para la descarga,
     * o un estado 404 si el archivo no se encuentra.
     */
    @GetMapping("/prestamos")
    fun exportCsvPrestamos(): ResponseEntity<ByteArray> {
        prestamoCsvStorage.generateAndSaveCsv()

        val fileName = "prestamos_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        return if (Files.exists(filePath)) {
            val fileContent = Files.readAllBytes(filePath)
            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
                add(HttpHeaders.CONTENT_TYPE, "text/csv")
            }
            ResponseEntity(fileContent, headers, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Exporta los datos de las sanciones a un archivo CSV y lo descarga.
     * El archivo se guarda temporalmente en el directorio "data".
     *
     * @return [ResponseEntity] con los bytes del archivo CSV y las cabeceras HTTP adecuadas para la descarga,
     * o un estado 404 si el archivo no se encuentra.
     */
    @GetMapping("/sanciones")
    fun exportCsvSanciones(): ResponseEntity<ByteArray> {
        sancionCsvStorage.generateAndSaveCsv()

        val fileName = "sanciones_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        return if (Files.exists(filePath)) {
            val fileContent = Files.readAllBytes(filePath)
            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
                add(HttpHeaders.CONTENT_TYPE, "text/csv")
            }
            ResponseEntity(fileContent, headers, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Exporta los datos de los usuarios a un archivo CSV y lo descarga.
     * El archivo se guarda temporalmente en el directorio "data".
     *
     * @return [ResponseEntity] con los bytes del archivo CSV y las cabeceras HTTP adecuadas para la descarga,
     * o un estado 404 si el archivo no se encuentra.
     */
    @GetMapping("/users")
    fun exportCsvUsers(): ResponseEntity<ByteArray> {
        userCsvStorage.generateAndSaveCsv()

        val fileName = "usuarios_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        return if (Files.exists(filePath)) {
            val fileContent = Files.readAllBytes(filePath)
            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
                add(HttpHeaders.CONTENT_TYPE, "text/csv")
            }
            ResponseEntity(fileContent, headers, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}