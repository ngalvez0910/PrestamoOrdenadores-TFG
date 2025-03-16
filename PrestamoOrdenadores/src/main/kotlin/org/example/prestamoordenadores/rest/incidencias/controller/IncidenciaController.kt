package org.example.prestamoordenadores.rest.incidencias.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError.IncidenciaNotFound
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError.UserNotFound
import org.example.prestamoordenadores.rest.incidencias.services.IncidenciaService
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

@RestController
@RequestMapping("/incidencias")
class IncidenciaController
@Autowired constructor(
    private val incidenciaService: IncidenciaService
)  {
    @GetMapping
    suspend fun getAllIncidencias() : ResponseEntity<Any>{
        return incidenciaService.getAllIncidencias().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/{guid}")
    suspend fun getIncidenciaByGuid(@PathVariable guid: String) : ResponseEntity<Any>{
        return incidenciaService.getIncidenciaByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencia no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/estado/{estado}")
    suspend fun getIncidenciaByEstado(@PathVariable estado: String): ResponseEntity<Any>{
        return incidenciaService.getIncidenciaByEstado(estado).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencias no encontradas")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/user/{guid}")
    suspend fun getIncidenciasByUserGuid(@PathVariable guid: String) : ResponseEntity<Any>{
        return incidenciaService.getIncidenciasByUserGuid(guid).mapBoth(
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
    suspend fun createIncidencia(@RequestBody incidencia: IncidenciaCreateRequest): ResponseEntity<Any> {
        return incidenciaService.createIncidencia(incidencia).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @PatchMapping("/{guid}")
    suspend fun updateIncidencia(@PathVariable guid: String, @RequestBody incidencia : IncidenciaUpdateRequest): ResponseEntity<Any>{
        return incidenciaService.updateIncidencia(guid, incidencia).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencia no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    suspend fun deleteIncidencia(@PathVariable guid: String): ResponseEntity<Any>{
        return incidenciaService.deleteIncidenciaByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencia no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }
}