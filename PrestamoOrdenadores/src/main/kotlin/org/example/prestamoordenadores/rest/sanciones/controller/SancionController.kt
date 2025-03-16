package org.example.prestamoordenadores.rest.sanciones.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError.SancionNotFound
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError.UserNotFound
import org.example.prestamoordenadores.rest.sanciones.services.SancionService
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
@RequestMapping("/sanciones")
class SancionController
@Autowired constructor(
    private val sancionService: SancionService
) {
    @GetMapping
    suspend fun getAllSanciones() : ResponseEntity<Any>{
        return sancionService.getAllSanciones().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/{guid}")
    suspend fun getSancionByGuid(@PathVariable guid: String) : ResponseEntity<Any>{
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
    suspend fun getSancionByFechaSancion(@PathVariable fecha: LocalDate) : ResponseEntity<Any>{
        return sancionService.getByFecha(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/user/{guid}")
    suspend fun getSancionesByUserGuid(@PathVariable guid: String) : ResponseEntity<Any>{
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
    suspend fun getSancionesByTipo(@PathVariable tipo: String): ResponseEntity<Any>{
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

    @PostMapping
    suspend fun createSancion(@RequestBody sancion : SancionRequest): ResponseEntity<Any>{
        return sancionService.createSancion(sancion).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when(error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PatchMapping("/{guid}")
    suspend fun updateSancion(@PathVariable guid: String, @RequestBody sancion : SancionUpdateRequest): ResponseEntity<Any>{
        return sancionService.updateSancion(guid, sancion).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    suspend fun deleteSancion(@PathVariable guid: String): ResponseEntity<Any>{
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