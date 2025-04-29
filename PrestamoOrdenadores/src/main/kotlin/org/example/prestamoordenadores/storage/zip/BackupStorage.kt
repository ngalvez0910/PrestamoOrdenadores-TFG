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

@Service
class BackupStorage {
    fun createDatabaseBackup(): Boolean {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        val backupDir = File("data/backup")
        backupDir.mkdirs()
        val backupFile = File(backupDir, "db_backup_$timestamp.sql")
        val dbHost = "localhost"
        val dbName = "prestamosDB"
        val dbUser = "admin"

        val pgpassFile = setupPgPassFile(dbHost, dbName, dbUser, "adminPassword123")

        try {
            val processBuilder = ProcessBuilder(
                "pg_dump",
                "-h", dbHost,
                "-U", dbUser,
                "-F", "p",
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

            outputReaderThread.join(3000)
            errorReaderThread.join(3000)

            pgpassFile?.delete()

            if (exitCode == 0) {
                logger.info { "Backup exitoso en: ${backupFile.absolutePath}" }
                return true
            } else {
                logger.error { "Error al realizar el backup. Código: $exitCode" }
                return false
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al ejecutar pg_dump: ${e.message}" }
            pgpassFile?.delete()
            return false
        }
    }

    fun restoreDatabaseBackup(backupFileName: String): Boolean {
        val backupFile = File("data/backup", backupFileName)
        if (!backupFile.exists() || !backupFile.isFile) {
            logger.error { "Archivo de backup no encontrado: ${backupFile.absolutePath}" }
            return false
        }

        val dbHost = "localhost"
        val dbName = "prestamosDB"
        val dbUser = "admin"

        val pgpassFile = setupPgPassFile(dbHost, dbName, dbUser, "adminPassword123")

        try {
            val processBuilder = ProcessBuilder(
                "psql",
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