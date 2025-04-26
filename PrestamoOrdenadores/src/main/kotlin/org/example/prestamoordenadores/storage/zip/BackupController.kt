package org.example.prestamoordenadores.storage.zip

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/backup")
@PreAuthorize("hasRole('ADMIN')")
class BackupController(
    private val backupStorage: BackupStorage
) {
    @GetMapping("/create")
    fun createBackup(): ResponseEntity<String?> {
        backupStorage.createDatabaseBackup()
        return ResponseEntity.ok("Copia de seguridad realizada con éxito.")
    }

    /*
    @PostMapping("/restore")
    fun uploadAndRestoreBackup(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, Any>> {
        val success = backupStorage.restoreBackup(file)

        return if (success) {
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Base de datos restaurada con éxito desde el archivo subido",
                "timestamp" to LocalDateTime.now().toString()
            ))
        } else {
            ResponseEntity.status(422).body(mapOf(
                "success" to false,
                "message" to "Error al restaurar la base de datos desde el archivo subido",
                "timestamp" to LocalDateTime.now().toString()
            ))
        }
    }*/

    @GetMapping("/list")
    fun listBackups(): ResponseEntity<List<Map<String, Any>>> {
        return try {
            val backups = backupStorage.listDatabaseBackups()
            ResponseEntity.ok(backups)
        } catch (e: Exception) {
            ResponseEntity.status(422).body(emptyList())
        }
    }
}