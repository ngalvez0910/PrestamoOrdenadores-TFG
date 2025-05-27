package org.example.prestamoordenadores.storage.excel

import org.lighthousegames.logging.logging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = logging()

@RestController
@RequestMapping("/storage/excel")
@PreAuthorize("hasRole('ADMIN')")
class ExcelStorageController(
    private val dispositivoExcelStorage: DispositivoExcelStorage,
    private val incidenciaExcelStorage: IncidenciaExcelStorage,
    private val prestamoExcelStorage: PrestamoExcelStorage,
    private val sancionExcelStorage: SancionExcelStorage,
    private val userExcelStorage: UserExcelStorage
) {

    @GetMapping("/dispositivos", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadDispositivosExcel(): ResponseEntity<ByteArray> {
        return try {
            logger.info { "Iniciando generación de Excel para dispositivos" }

            val excelData = dispositivoExcelStorage.generateExcel()
            logger.info { "Excel generado exitosamente. Tamaño: ${excelData.size} bytes" }

            val headers = HttpHeaders().apply {
                contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dispositivos.xlsx\"")
                contentLength = excelData.size.toLong()
            }

            logger.info { "Enviando respuesta con Excel de dispositivos" }
            ResponseEntity(excelData, headers, HttpStatus.OK)

        } catch (e: Exception) {
            logger.error { "Error generando Excel de dispositivos" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/incidencias", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadIncidenciasExcel(): ResponseEntity<ByteArray> {
        return try {
            logger.info { "Iniciando generación de Excel para incidencias" }

            val excelData = incidenciaExcelStorage.generateExcel()
            logger.info { "Excel de incidencias generado exitosamente. Tamaño: ${excelData.size} bytes" }

            val headers = HttpHeaders().apply {
                contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"incidencias.xlsx\"")
                contentLength = excelData.size.toLong()
            }

            ResponseEntity(excelData, headers, HttpStatus.OK)

        } catch (e: Exception) {
            logger.error { "Error generando Excel de incidencias" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/prestamos", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadPrestamosExcel(): ResponseEntity<ByteArray> {
        return try {
            logger.info { "Iniciando generación de Excel para préstamos" }

            val excelData = prestamoExcelStorage.generateExcel()
            logger.info { "Excel de préstamos generado exitosamente. Tamaño: ${excelData.size} bytes" }

            val headers = HttpHeaders().apply {
                contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"prestamos.xlsx\"")
                contentLength = excelData.size.toLong()
            }

            ResponseEntity(excelData, headers, HttpStatus.OK)

        } catch (e: Exception) {
            logger.error { "Error generando Excel de préstamos" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/sanciones", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadSancionesExcel(): ResponseEntity<ByteArray> {
        return try {
            logger.info { "Iniciando generación de Excel para sanciones" }

            val excelData = sancionExcelStorage.generateExcel()
            logger.info { "Excel de sanciones generado exitosamente. Tamaño: ${excelData.size} bytes" }

            val headers = HttpHeaders().apply {
                contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sanciones.xlsx\"")
                contentLength = excelData.size.toLong()
            }

            ResponseEntity(excelData, headers, HttpStatus.OK)

        } catch (e: Exception) {
            logger.error { "Error generando Excel de sanciones" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/users", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadUsersExcel(): ResponseEntity<ByteArray> {
        return try {
            logger.info { "Iniciando generación de Excel para usuarios" }

            val excelData = userExcelStorage.generateExcel()
            logger.info { "Excel de usuarios generado exitosamente. Tamaño: ${excelData.size} bytes" }

            val headers = HttpHeaders().apply {
                contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"usuarios.xlsx\"")
                contentLength = excelData.size.toLong()
            }

            ResponseEntity(excelData, headers, HttpStatus.OK)

        } catch (e: Exception) {
            logger.error { "Error generando Excel de usuarios" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}