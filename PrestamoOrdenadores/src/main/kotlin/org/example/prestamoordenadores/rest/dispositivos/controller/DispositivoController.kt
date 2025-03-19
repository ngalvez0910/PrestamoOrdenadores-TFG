package org.example.prestamoordenadores.rest.dispositivos.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError.DispositivoAlreadyExists
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError.DispositivoNotFound
import org.example.prestamoordenadores.rest.dispositivos.services.DispositivoService
import org.example.prestamoordenadores.storage.DispositivoCsvStorage
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
import org.springframework.http.HttpHeaders
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@RestController
@RequestMapping("/dispositivos")
class DispositivoController
@Autowired constructor(
    private val dispositivoService: DispositivoService,
    private val dispositivoCsvStorage: DispositivoCsvStorage
)  {
    @GetMapping
    suspend fun getAllDispositivos(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Any> {
        return dispositivoService.getAllDispositivos(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/{guid}")
    suspend fun getDispositivoByGuid(@PathVariable guid: String): ResponseEntity<Any>{
        return dispositivoService.getDispositivoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/numeroSerie/{numeroSerie}")
    suspend fun getDispositivoByNumeroSerie(@PathVariable numeroSerie: String): ResponseEntity<Any>{
        return dispositivoService.getDispositivoByNumeroSerie(numeroSerie).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/estado/{estado}")
    suspend fun getDispositivoByEstado(@PathVariable estado: String): ResponseEntity<Any>{
        return dispositivoService.getDispositivoByEstado(estado).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivos no encontrados")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PostMapping
    suspend fun createDispositivo(@RequestBody dispositivo: DispositivoCreateRequest): ResponseEntity<Any> {
        return dispositivoService.createDispositivo(dispositivo).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @PatchMapping("/{guid}")
    suspend fun updateDispositivo(@PathVariable guid: String, @RequestBody dispositivo : DispositivoUpdateRequest): ResponseEntity<Any>{
        return dispositivoService.updateDispositivo(guid, dispositivo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    is DispositivoAlreadyExists -> ResponseEntity.status(409).body("Dispositivo ya existente")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    suspend fun deleteDispositivo(@PathVariable guid: String): ResponseEntity<Any>{
        return dispositivoService.deleteDispositivoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when(error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/export/csv")
    fun exportCsv(): ResponseEntity<ByteArray> {
        dispositivoCsvStorage.generateAndSaveCsv()

        val fileName = "dispositivos_${LocalDate.now().toDefaultDateString()}.csv"
        val filePath = Paths.get("data", fileName)

        return if (Files.exists(filePath)) {
            val fileContent = Files.readAllBytes(filePath)
            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
                add(HttpHeaders.CONTENT_TYPE, "text/csv")
            }
            ResponseEntity(fileContent, headers, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}