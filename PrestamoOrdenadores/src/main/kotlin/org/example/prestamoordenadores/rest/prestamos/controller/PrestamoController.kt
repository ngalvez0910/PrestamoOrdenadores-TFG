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
import org.springframework.web.bind.annotation.DeleteMapping
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

@RestController
@RequestMapping("/prestamos")
@PreAuthorize("hasRole('ADMIN')")
class PrestamoController
@Autowired constructor(
    private val prestamoService: PrestamoService,
    private val prestamoPdfStorage: PrestamoPdfStorage,
) {
    @GetMapping
    fun getAllPrestamos(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Any> {
        return prestamoService.getAllPrestamos(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun getPrestamoByGuid(@PathVariable guid: String) : ResponseEntity<Any>{
        return prestamoService.getPrestamoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/fecha/{fecha}")
    fun getPrestamoByFechaPrestamo(@PathVariable fecha: LocalDate) : ResponseEntity<Any>{
        return prestamoService.getByFechaPrestamo(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/devoluciones/{fecha}")
    fun getPrestamoByFechaDevolucion(@PathVariable fecha: LocalDate) : ResponseEntity<Any>{
        return prestamoService.getByFechaDevolucion(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/user/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun getPrestamosByUserGuid(@PathVariable guid: String) : ResponseEntity<Any>{
        return prestamoService.getPrestamoByUserGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when(error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun createPrestamo(): ResponseEntity<Any>{
        return prestamoService.createPrestamo().mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when(error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    is PrestamoValidationError -> ResponseEntity.status(403).body("Préstamo inválido")
                    is DispositivoNotFound -> ResponseEntity.status(404).body("No hay dispositivos disponibles actualmente")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PatchMapping("/{guid}")
    fun updatePrestamo(@PathVariable guid: String, @RequestBody prestamo : PrestamoUpdateRequest): ResponseEntity<Any>{
        return prestamoService.updatePrestamo(guid, prestamo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    is PrestamoValidationError -> ResponseEntity.status(403).body("Préstamo inválido")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deletePrestamo(@PathVariable guid: String): ResponseEntity<Any>{
        return prestamoService.deletePrestamoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

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
}