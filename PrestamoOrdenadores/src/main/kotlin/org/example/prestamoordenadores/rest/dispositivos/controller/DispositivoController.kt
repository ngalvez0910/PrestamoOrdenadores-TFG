package org.example.prestamoordenadores.rest.dispositivos.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError.DispositivoNotFound
import org.example.prestamoordenadores.rest.dispositivos.services.DispositivoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dispositivos")
class DispositivoController
@Autowired constructor(
    private val dispositivoService: DispositivoService
)  {
    @GetMapping
    fun getAllDispositivos(): ResponseEntity<List<DispositivoResponse>>{
        return dispositivoService.getAllDispositivos().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @GetMapping("/{guid}")
    fun getDispositivoByGuid(@PathVariable guid: String): ResponseEntity<DispositivoResponse?>{
        return dispositivoService.getDispositivoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/numeroSerie/{numeroSerie}")
    fun getDispositivoByNumeroSerie(@PathVariable numeroSerie: String): ResponseEntity<DispositivoResponseAdmin?>{
        return dispositivoService.getDispositivoByNumeroSerie(numeroSerie).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/estado/{estado}")
    fun getDispositivoByEstado(@PathVariable estado: String): ResponseEntity<List<DispositivoResponseAdmin>>{
        return dispositivoService.getDispositivoByEstado(estado).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @PostMapping
    fun createDispositivo(@RequestBody dispositivo: DispositivoCreateRequest): ResponseEntity<DispositivoResponse> {
        return dispositivoService.createDispositivo(dispositivo).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @PutMapping("/{guid}")
    fun updateDispositivo(@PathVariable guid: String, @RequestBody dispositivo : DispositivoUpdateRequest): ResponseEntity<DispositivoResponse?>{
        return dispositivoService.updateDispositivo(guid, dispositivo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deleteDispositivo(@PathVariable guid: String): ResponseEntity<DispositivoResponse?>{
        return dispositivoService.deleteDispositivoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }
}