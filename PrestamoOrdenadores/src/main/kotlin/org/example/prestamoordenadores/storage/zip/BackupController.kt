package org.example.prestamoordenadores.storage.zip

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
@RestController
@RequestMapping("/backup")
class BackupController(
    private val backupStorage: BackupStorage
) {

    @GetMapping("/create")
    fun createBackup(): ResponseEntity<String?> {
        backupStorage.createDatabaseBackup()
        return ResponseEntity.ok("Copia de seguridad realizada con éxito.")
    }
}
*/