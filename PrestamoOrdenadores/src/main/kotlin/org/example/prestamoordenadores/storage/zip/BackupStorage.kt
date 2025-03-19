package org.example.prestamoordenadores.storage.zip

import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Service
import java.io.*
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/*
@Service
class BackupStorage {
    fun createDatabaseBackup() {
        // 1. Crear el respaldo con pg_dump
        val processBuilder = ProcessBuilder("C:\\Program Files\\PostgreSQL\\17\\bin\\pg_dump.exe", "-U", "admin", "-d", "prestamosDB", "-f", "data/backup_${LocalDate.now().toDefaultDateString()}.sql")
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()

        if (!process.waitFor(300000, TimeUnit.MILLISECONDS)) {
            println("El proceso se ha excedido del tiempo máximo permitido.")
            process.destroy()  // Terminar el proceso si excede el tiempo
        } else {
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.lines().forEach { println(it) }
            val exitCode = process.exitValue()
            if (exitCode != 0) {
                println("Error ejecutando pg_dump")
            }
        }

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.lines().forEach { println(it) }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            println("Error ejecutando pg_dump")
            return
        }

        println("Respaldo de base de datos creado con éxito")

        // 2. Crear el archivo ZIP
        try {
            val zipFile = File("data/backup_${LocalDate.now().toDefaultDateString()}.zip")
            val zipOut = ZipOutputStream(FileOutputStream(zipFile))

            // Añadir el archivo .sql al ZIP
            val sqlFile = File("data/backup_${LocalDate.now().toDefaultDateString()}.sql")
            val zipEntry = ZipEntry(sqlFile.name)
            zipOut.putNextEntry(zipEntry)

            val inputStream = FileInputStream(sqlFile)
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
                zipOut.write(buffer, 0, len)
            }

            inputStream.close()
            zipOut.closeEntry()
            zipOut.close()

            println("Archivo ZIP creado: ${zipFile.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error creando el archivo ZIP")
        }

        // 3. Eliminar el archivo .sql temporal (opcional)
        val sqlFile = File("data/backup${LocalDate.now().toDefaultDateString()}.sql")
        if (sqlFile.exists()) {
            sqlFile.delete()
            println("Archivo .sql temporal eliminado")
        }
    }
}*/