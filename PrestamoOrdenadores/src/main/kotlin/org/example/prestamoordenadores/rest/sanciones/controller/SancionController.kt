package org.example.prestamoordenadores.rest.sanciones.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError.SancionNotFound
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError.SancionValidationError
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError.UserNotFound
import org.example.prestamoordenadores.rest.sanciones.services.SancionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/sanciones")
@PreAuthorize("hasRole('ADMIN')")
class SancionController
@Autowired constructor(
    private val sancionService: SancionService,
) {
    @GetMapping
    fun getAllSanciones(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Any> {
        return sancionService.getAllSanciones(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/{guid}")
    fun getSancionByGuid(@PathVariable guid: String) : ResponseEntity<Any>{
        return sancionService.getSancionByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/fecha/{fecha}")
    fun getSancionByFechaSancion(@PathVariable fecha: LocalDate) : ResponseEntity<Any>{
        return sancionService.getByFecha(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/user/{guid}")
    fun getSancionesByUserGuid(@PathVariable guid: String) : ResponseEntity<Any>{
        return sancionService.getSancionByUserGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when(error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/tipo/{tipo}")
    fun getSancionesByTipo(@PathVariable tipo: String): ResponseEntity<Any>{
        return sancionService.getByTipo(tipo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanciones no encontradas")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PatchMapping("/{guid}")
    fun updateSancion(@PathVariable guid: String, @RequestBody sancion : SancionUpdateRequest): ResponseEntity<Any>{
        return sancionService.updateSancion(guid, sancion).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    is SancionValidationError -> ResponseEntity.status(403).body("Sanción inválida")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deleteSancion(@PathVariable guid: String): ResponseEntity<Any>{
        return sancionService.deleteSancionByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }
}