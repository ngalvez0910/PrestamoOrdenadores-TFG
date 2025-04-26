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
                "-Fc",
                "-h", dbHost,
                "-U", dbUser,
                "-d", dbName,
                "-f", backupFile.absolutePath
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
                        logger.debug { "pg_dump stdout: $line" }
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

    /*
    fun restoreBackup(file: MultipartFile): Boolean {
        if (file.isEmpty) {
            logger.error { "El archivo subido está vacío" }
            return false
        }

        if (!file.originalFilename?.endsWith(".sql", ignoreCase = true)!!) {
            logger.error { "El archivo debe ser un archivo SQL (.sql)" }
            return false
        }

        val currentDir = System.getProperty("user.dir")

        val backupDir = Paths.get(currentDir, "data", "backup")

        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir)
        }

        val fileName = "uploaded_backup_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))}.sql"
        val filePath = backupDir.resolve(fileName)

        return try {
            logger.debug { "Intentando guardar el backup en: $filePath" }
            file.transferTo(filePath.toFile())
            val success = restoreDatabaseBackup(filePath.toFile())

            try {
                filePath.toFile().delete()
            } catch (e: Exception) {
                logger.warn { "Error al intentar borrar el archivo temporal de restauración: ${e.message}" }
            }

            success
        } catch (e: Exception) {
            logger.error(e) { "Error al procesar y guardar el archivo subido: ${e.message}" }
            false
        }
    }

    private fun restoreDatabaseBackup(backupFile: File? = null): Boolean {
        val dbHost = "localhost"
        val dbName = "prestamosDB"
        val dbUser = "admin"

        val fileToRestore = backupFile

        if (fileToRestore == null || !fileToRestore.exists()) {
            logger.error { "No se encontró ningún archivo de backup para restaurar" }
            return false
        }

        logger.info { "Restaurando base de datos desde: ${fileToRestore.absolutePath}" }

        val pgpassFile = setupPgPassFile(dbHost, dbName, dbUser, "tu_contraseña_aquí")

        try {
            //dropConnections(dbHost, dbName, dbUser, pgpassFile)

            val processBuilder = ProcessBuilder(
                "pg_restore",
                "-h", dbHost,
                "-U", dbUser,
                "-d", dbName,
                "-j", "4",
                fileToRestore.absolutePath
            )

            if (pgpassFile != null) {
                processBuilder.environment()["DATABASE_PASSWORD_POSTGRES"] = pgpassFile.absolutePath
            }

            logger.info { "Ejecutando restauración de la base de datos..." }
            val process = processBuilder.start()

            val outputReaderThread = thread {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        logger.info { "psql: $line" }
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
                            logger.error { "psql: $line" }
                        } else {
                            logger.info { "psql: $line" }
                        }
                    }
                }
            }

            val exitCode = process.waitFor()

            outputReaderThread.join(3000)
            errorReaderThread.join(3000)

            pgpassFile?.delete()

            if (exitCode == 0) {
                logger.info { "Restauración exitosa desde: ${fileToRestore.absolutePath}" }
                return true
            } else {
                logger.error { "Error al restaurar la base de datos. Código: $exitCode" }
                return false
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al ejecutar la restauración: ${e.message}" }
            pgpassFile?.delete()
            return false
        }
    }*/

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

    /*
    private fun dropConnections(host: String, database: String, user: String, pgpassFile: File?): Boolean {
        try {
            val sql = """
                SELECT pg_terminate_backend(pid)
                FROM pg_stat_activity
                WHERE datname = '$database'
                AND pid <> pg_backend_pid()
            """.trimIndent()

            val processBuilder = ProcessBuilder(
                "psql",
                "-h", host,
                "-U", user,
                "-d", "postgres",
                "-c", sql
            )

            if (pgpassFile != null) {
                processBuilder.environment()["PGPASSFILE"] = pgpassFile.absolutePath
            }

            logger.info { "Cerrando conexiones activas a la base de datos..." }
            val process = processBuilder.start()

            val exitCode = process.waitFor()

            return exitCode == 0
        } catch (e: Exception) {
            logger.error { "Error al cerrar conexiones: ${e.message}" }
            return false
        }
    }*/

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