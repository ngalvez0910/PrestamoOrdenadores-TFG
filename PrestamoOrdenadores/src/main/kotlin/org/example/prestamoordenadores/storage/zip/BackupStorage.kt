package org.example.prestamoordenadores.storage.zip

import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.concurrent.thread

private val logger = logging()

/**
 * Servicio para la gestión de copias de seguridad y restauración de la base de datos PostgreSQL.
 *
 * Esta clase se encarga de interactuar con las utilidades de línea de comandos `pg_dump` y `psql`
 * para crear y restaurar backups de la base de datos, así como para listar los backups existentes.
 * También gestiona un archivo temporal `.pgpass` para el manejo seguro de credenciales.
 *
 * @author Natalia González Álvarez
 */
@Service
class BackupStorage {
    /**
     * Crea una copia de seguridad de la base de datos PostgreSQL.
     *
     * Genera un archivo de respaldo SQL en el directorio `data/backup` con un nombre basado en la fecha y hora.
     * Utiliza `pg_dump` para realizar la copia de seguridad. Se filtra una línea específica
     * ("SET transaction_timeout = 0;") del archivo de backup generado.
     *
     * @return `true` si la copia de seguridad se realizó con éxito, `false` en caso contrario.
     */
    fun createDatabaseBackup(): Boolean {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        val backupDir = File("data/backup")
        backupDir.mkdirs()
        val backupFile = File(backupDir, "db_backup_$timestamp.sql")
        val dbHost = "postgres-db"
        val dbName = "prestamosDB"
        val dbUser = "admin"

        val pgpassFile = setupPgPassFile(dbHost, dbName, dbUser, "adminPassword123")

        try {
            val processBuilder = ProcessBuilder(
                "pg_dump",
                "-h", dbHost,
                "-U", dbUser,
                "-F", "p",
                "--clean",
                "--if-exists",
                "-b",
                "-v",
                "-f", backupFile.absolutePath,
                dbName
            )

            if (pgpassFile != null) {
                processBuilder.environment()["PGPASSFILE"] = pgpassFile.absolutePath
            }

            logger.info { "Ejecutando backup de la base de datos..." }
            val process = processBuilder.start()

            val outputReaderThread = thread {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        logger.debug { "pg_dump: $line" }
                    }
                }
            }

            val errorReaderThread = thread {
                BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line?.contains("error", ignoreCase = true) == true ||
                            line?.contains("ERROR", ignoreCase = true) == true ||
                            line?.contains("fatal", ignoreCase = true) == true) {
                            logger.error { "pg_dump: $line" }
                        } else {
                            logger.info { "pg_dump: $line" }
                        }
                    }
                }
            }

            val exitCode = process.waitFor()

            outputReaderThread.join(5000)
            errorReaderThread.join(5000)

            pgpassFile?.delete()

            if (exitCode == 0) {
                logger.info { "Backup exitoso en: ${backupFile.absolutePath}" }
                try {
                    val lines = backupFile.readLines(Charsets.UTF_8)

                    val filteredLines = lines.filterNot { line ->
                        line.trim().equals("SET transaction_timeout = 0;", ignoreCase = true)
                    }

                    if (filteredLines.size < lines.size) {
                        backupFile.writeText(filteredLines.joinToString(System.lineSeparator()), Charsets.UTF_8)
                        logger.info { "Archivo de backup '${backupFile.name}' filtrado para remover 'SET transaction_timeout = 0;'." }
                    } else {
                        logger.info { "La línea 'SET transaction_timeout = 0;' no se encontró en '${backupFile.name}'. No se realizaron cambios de filtrado." }
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Error al filtrar el archivo de backup para remover transaction_timeout: ${e.message}" }
                }
                return true
            } else {
                logger.error { "Error al realizar el backup. Código: $exitCode" }
                return false
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al ejecutar pg_dump: ${e.message}" }
            pgpassFile?.delete()
            if (backupFile.exists()) {
                backupFile.delete()
            }
            return false
        }
    }

    /**
     * Restaura la base de datos PostgreSQL a partir de un archivo de copia de seguridad.
     *
     * Utiliza `psql` para ejecutar el script SQL del archivo de respaldo.
     *
     * @param backupFileName El nombre del archivo de la copia de seguridad a restaurar,
     * esperado en el directorio `data/backup`.
     * @return `true` si la restauración se realizó con éxito, `false` en caso contrario.
     */
    fun restoreDatabaseBackup(backupFileName: String): Boolean {
        val backupFile = File("data/backup", backupFileName)
        if (!backupFile.exists() || !backupFile.isFile) {
            logger.error { "Archivo de backup no encontrado: ${backupFile.absolutePath}" }
            return false
        }

        val dbHost = "postgres-db"
        val dbName = "prestamosDB"
        val dbUser = "admin"

        val pgpassFile = setupPgPassFile(dbHost, dbName, dbUser, "adminPassword123")

        try {
            val processBuilder = ProcessBuilder(
                "psql",
                "-1",
                "-v", "ON_ERROR_STOP=1",
                "-h", dbHost,
                "-U", dbUser,
                "-d", dbName,
                "-f", backupFile.absolutePath
            )

            if (pgpassFile != null) {
                processBuilder.environment()["PGPASSFILE"] = pgpassFile.absolutePath
            }

            logger.info { "Restaurando la base de datos desde: ${backupFile.absolutePath}" }
            val process = processBuilder.start()

            val outputReaderThread = thread {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        logger.debug { "pg_restore stdout: $line" }
                    }
                }
            }

            val errorReaderThread = thread {
                BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line?.contains("error", ignoreCase = true) == true ||
                            line?.contains("ERROR", ignoreCase = true) == true ||
                            line?.contains("fatal", ignoreCase = true) == true) {
                            logger.error { "pg_restore: $line" }
                        } else {
                            logger.info { "pg_restore: $line" }
                        }
                    }
                }
            }

            val exitCode = process.waitFor()

            outputReaderThread.join(3000)
            errorReaderThread.join(3000)

            pgpassFile?.delete()

            if (exitCode == 0) {
                logger.info { "Restauración exitosa de la base de datos desde: ${backupFile.absolutePath}" }
                return true
            } else {
                logger.error { "Error al restaurar la base de datos. Código: $exitCode" }
                return false
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al ejecutar pg_restore: ${e.message}" }
            pgpassFile?.delete()
            return false
        }
    }

    /**
     * Lista las copias de seguridad de la base de datos disponibles en el directorio `data/backup`.
     *
     * @return Una lista de mapas, donde cada mapa representa un archivo de copia de seguridad con
     * su nombre, tamaño y fecha de última modificación. La lista se ordena por nombre descendente.
     */
    fun listDatabaseBackups(): List<Map<String, Any>> {
        val backupDir = File("data/backup")

        if (!backupDir.exists() || !backupDir.isDirectory) {
            logger.info { "No se encontró el directorio de backups o no es un directorio." }
            return emptyList()
        }

        val backupFiles = backupDir.listFiles { file ->
            file.isFile && file.name.endsWith(".sql")
        }?.map {
            mapOf(
                "name" to it.name,
                "size" to it.length(),
                "date" to Date(it.lastModified()).toString()
            )
        }?.sortedByDescending {
            (it["name"] as String)
        } ?: emptyList()

        logger.info { "Se encontraron ${backupFiles.size} archivos de backup." }
        return backupFiles
    }

    /**
     * Configura y crea un archivo `.pgpass` temporal para autenticación no interactiva con PostgreSQL.
     *
     * Este archivo contiene las credenciales de la base de datos en un formato específico
     * y se utiliza para evitar solicitar la contraseña al ejecutar `pg_dump` o `psql`.
     * Se configura para tener permisos de solo lectura para el propietario.
     *
     * @param host El host de la base de datos.
     * @param database El nombre de la base de datos.
     * @param user El usuario de la base de datos.
     * @param password La contraseña del usuario de la base de datos.
     * @return Un objeto [File] que apunta al archivo `.pgpass` temporal, o `null` si ocurre un error.
     */
    private fun setupPgPassFile(host: String, database: String, user: String, password: String): File? {
        try {
            val tempFile = File.createTempFile("pgpass", ".conf")
            tempFile.setReadable(false, false)
            tempFile.setReadable(true, true)
            tempFile.setWritable(false, false)
            tempFile.setWritable(true, true)

            tempFile.writeText("$host:5432:$database:$user:$password")
            return tempFile
        } catch (e: Exception) {
            logger.error { "Error al crear archivo pgpass: ${e.message}" }
            return null
        }
    }
}