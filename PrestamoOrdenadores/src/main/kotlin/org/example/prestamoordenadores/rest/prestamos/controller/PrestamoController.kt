package org.example.prestamoordenadores.rest.prestamos.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.DispositivoNotFound
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.PrestamoNotFound
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.PrestamoValidationError
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.UserNotFound
import org.example.prestamoordenadores.rest.prestamos.services.PrestamoService
import org.example.prestamoordenadores.storage.pdf.PrestamoPdfStorage
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

/**
 * Controlador REST para la gestión de préstamos de ordenadores.
 *
 * Este controlador expone endpoints para realizar operaciones CRUD y consultas relacionadas con los préstamos.
 * La mayoría de las operaciones requieren el rol 'ADMIN', aunque algunas de consulta y creación
 * están disponibles para 'ALUMNO' y 'PROFESOR'.
 *
 * @property prestamoService El servicio de préstamos encargado de la lógica de negocio.
 * @property prestamoPdfStorage El servicio para la generación y almacenamiento de PDFs de préstamos.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/prestamos")
@PreAuthorize("hasRole('ADMIN')")
class PrestamoController
@Autowired constructor(
    private val prestamoService: PrestamoService,
    private val prestamoPdfStorage: PrestamoPdfStorage,
) {
    /**
     * Obtiene una lista paginada de todos los préstamos.
     *
     * @param page El número de página (por defecto 0).
     * @param size El tamaño de la página (por defecto 5).
     * @return [ResponseEntity] con una lista paginada de préstamos o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping
    fun getAllPrestamos(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "5") size: Int): ResponseEntity<Any> {
        return prestamoService.getAllPrestamos(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    /**
     * Obtiene un préstamo por su identificador único global (GUID).
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @param guid El GUID del préstamo.
     * @return [ResponseEntity] con el préstamo encontrado o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun getPrestamoByGuid(@PathVariable guid: String): ResponseEntity<Any> {
        return prestamoService.getPrestamoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene una lista de préstamos por su fecha de inicio.
     *
     * @param fecha La fecha de préstamo a buscar.
     * @return [ResponseEntity] con una lista de préstamos o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/fecha/{fecha}")
    fun getPrestamoByFechaPrestamo(@PathVariable fecha: LocalDate): ResponseEntity<Any> {
        return prestamoService.getByFechaPrestamo(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    /**
     * Obtiene una lista de préstamos por su fecha de devolución.
     *
     * @param fecha La fecha de devolución a buscar.
     * @return [ResponseEntity] con una lista de préstamos o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/devoluciones/{fecha}")
    fun getPrestamoByFechaDevolucion(@PathVariable fecha: LocalDate): ResponseEntity<Any> {
        return prestamoService.getByFechaDevolucion(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    /**
     * Obtiene una lista de préstamos asociados a un usuario específico por su GUID.
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @param guid El GUID del usuario.
     * @return [ResponseEntity] con una lista de préstamos o un mensaje de error si el usuario no se encuentra.
     * @author Natalia González Álvarez
     */
    @GetMapping("/user/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun getPrestamosByUserGuid(@PathVariable guid: String): ResponseEntity<Any> {
        return prestamoService.getPrestamoByUserGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Crea un nuevo préstamo.
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @return [ResponseEntity] con el préstamo creado o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun createPrestamo(): ResponseEntity<Any> {
        return prestamoService.createPrestamo().mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    is PrestamoValidationError -> ResponseEntity.status(400).body("Préstamo inválido")
                    is DispositivoNotFound -> ResponseEntity.status(404).body("No hay dispositivos disponibles actualmente")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Actualiza el estado o la fecha de devolución de un préstamo existente.
     *
     * @param guid El GUID del préstamo a actualizar.
     * @param prestamo La solicitud [PrestamoUpdateRequest] con los campos a actualizar.
     * @return [ResponseEntity] con el préstamo actualizado o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/{guid}")
    fun updatePrestamo(@PathVariable guid: String, @RequestBody prestamo: PrestamoUpdateRequest): ResponseEntity<Any> {
        return prestamoService.updatePrestamo(guid, prestamo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    is PrestamoValidationError -> ResponseEntity.status(400).body("Préstamo inválido")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Elimina lógicamente un préstamo (lo marca como no disponible).
     *
     * @param guid El GUID del préstamo a eliminar lógicamente.
     * @return [ResponseEntity] con el préstamo marcado como eliminado o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/delete/{guid}")
    fun deletePrestamo(@PathVariable guid: String): ResponseEntity<Any> {
        return prestamoService.deletePrestamoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Genera y descarga un PDF de un préstamo específico.
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @param guid El GUID del préstamo para el cual generar el PDF.
     * @return [ResponseEntity] con el contenido del PDF como un array de bytes.
     * @author Natalia González Álvarez
     */
    @GetMapping("/export/pdf/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun generateAndSavePdf(@PathVariable guid: String): ResponseEntity<ByteArray> {
        val pdfBytes = prestamoPdfStorage.generatePdf(guid)
        val fileName = "prestamo_${LocalDate.now().toDefaultDateString()}.pdf"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.contentDisposition = org.springframework.http.ContentDisposition.builder("attachment").filename(fileName).build()

        return ResponseEntity(pdfBytes, headers, HttpStatus.OK)
    }

    /**
     * Cancela un préstamo específico.
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @param guid El GUID del préstamo a cancelar.
     * @return [ResponseEntity] con el préstamo cancelado o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/cancelar/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun cancelarPrestamo(@PathVariable guid: String): ResponseEntity<Any> {
        return prestamoService.cancelarPrestamo(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }
}