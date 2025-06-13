package org.example.prestamoordenadores.storage.zip

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador REST para la gestión de copias de seguridad de la base de datos.
 *
 * Este controlador proporciona endpoints para crear, listar y restaurar copias de seguridad de la base de datos.
 * Todas las operaciones en este controlador requieren que el usuario tenga el rol 'ADMIN'.
 *
 * @property backupStorage El servicio de almacenamiento de copias de seguridad utilizado para interactuar con los backups.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/backup")
@PreAuthorize("hasRole('ADMIN')")
class BackupController(
    private val backupStorage: BackupStorage
) {
    /**
     * Endpoint para crear una nueva copia de seguridad de la base de datos.
     *
     * @return [ResponseEntity] con un mensaje de éxito si la copia de seguridad se realiza correctamente.
     */
    @GetMapping("/create")
    fun createBackup(): ResponseEntity<String?> {
        backupStorage.createDatabaseBackup()
        return ResponseEntity.ok("Copia de seguridad realizada con éxito.")
    }

    /**
     * Endpoint para listar todas las copias de seguridad de la base de datos disponibles.
     *
     * @return [ResponseEntity] con una lista de mapas, donde cada mapa representa una copia de seguridad
     * (por ejemplo, nombre del archivo y fecha de creación), o un estado 422 si hay un error.
     */
    @GetMapping("/list")
    fun listBackups(): ResponseEntity<List<Map<String, Any>>> {
        return try {
            val backups = backupStorage.listDatabaseBackups()
            ResponseEntity.ok(backups)
        } catch (e: Exception) {
            ResponseEntity.status(422).body(emptyList())
        }
    }

    /**
     * Endpoint para restaurar la base de datos a partir de una copia de seguridad específica.
     *
     * @param fileName El nombre del archivo de la copia de seguridad a restaurar.
     * @return [ResponseEntity] con un mensaje de éxito si la restauración se realiza correctamente,
     * o un mensaje de error con estado 500 si falla.
     */
    @PostMapping("/restore")
    fun restoreBackup(@RequestParam("fileName") fileName: String): ResponseEntity<String> {
        return if (backupStorage.restoreDatabaseBackup(fileName)) {
            ResponseEntity.ok("Restauración desde $fileName realizada con éxito.")
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al restaurar desde $fileName.")
        }
    }
}