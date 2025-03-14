package org.example.prestamoordenadores.rest.prestamos.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.PrestamoNotFound
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError.UserNotFound
import org.example.prestamoordenadores.rest.prestamos.services.PrestamoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/prestamos")
class PrestamoController
@Autowired constructor(
    private val prestamoService: PrestamoService
) {
    @GetMapping
    fun getAllPrestamos() : ResponseEntity<List<PrestamoResponse>>{
        return prestamoService.getAllPrestamos().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @GetMapping("/{guid}")
    fun getPrestamoByGuid(@PathVariable guid: String) : ResponseEntity<PrestamoResponse?>{
        return prestamoService.getPrestamoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is PrestamoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/{fecha}")
    fun getPrestamoByFechaPrestamo(@PathVariable fecha: LocalDate) : ResponseEntity<List<PrestamoResponse>>{
        return prestamoService.getByFechaPrestamo(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @GetMapping("/devoluciones/{fecha}")
    fun getPrestamoByFechaDevolucion(@PathVariable fecha: LocalDate) : ResponseEntity<List<PrestamoResponse>>{
        return prestamoService.getByFechaDevolucion(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @GetMapping("/user/{guid}")
    fun getPrestamosByUserGuid(@PathVariable guid: String) : ResponseEntity<List<PrestamoResponse>>{
        return prestamoService.getPrestamoByUserGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when(error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @PostMapping
    fun createPrestamo(@RequestBody prestamo : PrestamoCreateRequest): ResponseEntity<PrestamoResponse>{
        return prestamoService.createPrestamo(prestamo).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @PatchMapping("/{guid}")
    fun updatePrestamo(@PathVariable guid: String, @RequestBody prestamo : PrestamoUpdateRequest): ResponseEntity<PrestamoResponse?>{
        return prestamoService.updatePrestamo(guid, prestamo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is PrestamoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deletePrestamo(@PathVariable guid: String): ResponseEntity<PrestamoResponse?>{
        return prestamoService.deletePrestamoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is PrestamoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }
}