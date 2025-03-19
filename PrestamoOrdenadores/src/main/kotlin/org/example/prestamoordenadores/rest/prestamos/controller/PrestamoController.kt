package org.example.prestamoordenadores.rest.prestamos.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.DispositivoNotFound
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.PrestamoNotFound
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.UserNotFound
import org.example.prestamoordenadores.rest.prestamos.services.PrestamoService
import org.example.prestamoordenadores.storage.PrestamoPdfStorage
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
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

@RestController
@RequestMapping("/prestamos")
class PrestamoController
@Autowired constructor(
    private val prestamoService: PrestamoService,
    private val prestamoPdfStorage: PrestamoPdfStorage
) {
    @GetMapping
    suspend fun getAllPrestamos(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Any> {
        return prestamoService.getAllPrestamos(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/{guid}")
    suspend fun getPrestamoByGuid(@PathVariable guid: String) : ResponseEntity<Any>{
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
    suspend fun getPrestamoByFechaPrestamo(@PathVariable fecha: LocalDate) : ResponseEntity<Any>{
        return prestamoService.getByFechaPrestamo(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/devoluciones/{fecha}")
    suspend fun getPrestamoByFechaDevolucion(@PathVariable fecha: LocalDate) : ResponseEntity<Any>{
        return prestamoService.getByFechaDevolucion(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/user/{guid}")
    suspend fun getPrestamosByUserGuid(@PathVariable guid: String) : ResponseEntity<Any>{
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
    suspend fun createPrestamo(@RequestBody prestamo : PrestamoCreateRequest): ResponseEntity<Any>{
        return prestamoService.createPrestamo(prestamo).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when(error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    is DispositivoNotFound -> ResponseEntity.status(404).body("No hay dispositivos disponibles actualmente")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PatchMapping("/{guid}")
    suspend fun updatePrestamo(@PathVariable guid: String, @RequestBody prestamo : PrestamoUpdateRequest): ResponseEntity<Any>{
        return prestamoService.updatePrestamo(guid, prestamo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is PrestamoNotFound -> ResponseEntity.status(404).body("Préstamo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    suspend fun deletePrestamo(@PathVariable guid: String): ResponseEntity<Any>{
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
    fun generateAndSavePdf(@PathVariable guid: String): ResponseEntity<String> {
        val fileName = "prestamo_${LocalDate.now().toDefaultDateString()}.pdf"
        prestamoPdfStorage.generateAndSavePdf(guid)

        return ResponseEntity.ok("El PDF ha sido guardado exitosamente en la carpeta 'data' con el nombre $fileName")
    }
}