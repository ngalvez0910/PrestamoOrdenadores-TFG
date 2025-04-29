package org.example.prestamoordenadores.storage.zip

import org.springframework.http.HttpStatus
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

    @GetMapping("/list")
    fun listBackups(): ResponseEntity<List<Map<String, Any>>> {
        return try {
            val backups = backupStorage.listDatabaseBackups()
            ResponseEntity.ok(backups)
        } catch (e: Exception) {
            ResponseEntity.status(422).body(emptyList())
        }
    }

    @PostMapping("/restore")
    fun restoreBackup(@RequestParam("fileName") fileName: String): ResponseEntity<String> {
        return if (backupStorage.restoreDatabaseBackup(fileName)) {
            ResponseEntity.ok("Restauración desde $fileName realizada con éxito.")
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al restaurar desde $fileName.")
        }
    }
}