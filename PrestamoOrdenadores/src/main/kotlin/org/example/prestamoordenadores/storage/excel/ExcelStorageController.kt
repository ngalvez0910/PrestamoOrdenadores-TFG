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

/**
 * Controlador REST para la descarga de archivos Excel de diferentes entidades del sistema.
 *
 * Este controlador proporciona endpoints para generar y descargar archivos Excel que contienen
 * datos de dispositivos, incidencias, préstamos, sanciones y usuarios.
 * Todas las operaciones en este controlador requieren que el usuario tenga el rol 'ADMIN'.
 *
 * @property dispositivoExcelStorage Servicio para generar Excel de dispositivos.
 * @property incidenciaExcelStorage Servicio para generar Excel de incidencias.
 * @property prestamoExcelStorage Servicio para generar Excel de préstamos.
 * @property sancionExcelStorage Servicio para generar Excel de sanciones.
 * @property userExcelStorage Servicio para generar Excel de usuarios.
 * @author Natalia González Álvarez
 */
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

    /**
     * Descarga un archivo Excel que contiene todos los dispositivos.
     *
     * @return [ResponseEntity] con los bytes del archivo Excel y las cabeceras HTTP adecuadas para la descarga,
     * o un estado **204 No Content** si ocurre un error durante la generación, para evitar un error 500.
     */
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
            logger.error { "Error generando Excel de dispositivos: ${e.message}" }
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }

    /**
     * Descarga un archivo Excel que contiene todas las incidencias.
     *
     * @return [ResponseEntity] con los bytes del archivo Excel y las cabeceras HTTP adecuadas para la descarga,
     * o un estado **204 No Content** si ocurre un error durante la generación, para evitar un error 500.
     */
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
            logger.error { "Error generando Excel de incidencias: ${e.message}" }
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }

    /**
     * Descarga un archivo Excel que contiene todos los préstamos.
     *
     * @return [ResponseEntity] con los bytes del archivo Excel y las cabeceras HTTP adecuadas para la descarga,
     * o un estado **204 No Content** si ocurre un error durante la generación, para evitar un error 500.
     */
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
            logger.error { "Error generando Excel de préstamos: ${e.message}" }
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }

    /**
     * Descarga un archivo Excel que contiene todas las sanciones.
     *
     * @return [ResponseEntity] con los bytes del archivo Excel y las cabeceras HTTP adecuadas para la descarga,
     * o un estado **204 No Content** si ocurre un error durante la generación, para evitar un error 500.
     */
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
            logger.error { "Error generando Excel de sanciones: ${e.message}" }
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }

    /**
     * Descarga un archivo Excel que contiene todos los usuarios.
     *
     * @return [ResponseEntity] con los bytes del archivo Excel y las cabeceras HTTP adecuadas para la descarga,
     * o un estado **204 No Content** si ocurre un error durante la generación, para evitar un error 500.
     */
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
            logger.error { "Error generando Excel de usuarios: ${e.message}" }
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }
}