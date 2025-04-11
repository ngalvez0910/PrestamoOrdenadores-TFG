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