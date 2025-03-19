package org.example.prestamoordenadores.rest.incidencias.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError.IncidenciaNotFound
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError.UserNotFound
import org.example.prestamoordenadores.rest.incidencias.services.IncidenciaService
import org.example.prestamoordenadores.storage.csv.IncidenciaCsvStorage
import org.example.prestamoordenadores.storage.pdf.IncidenciaPdfStorage
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
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
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

@RestController
@RequestMapping("/incidencias")
class IncidenciaController
@Autowired constructor(
    private val incidenciaService: IncidenciaService,
    private val incidenciaCsvStorage: IncidenciaCsvStorage,
    private val incidenciaPdfStorage: IncidenciaPdfStorage
)  {
    @GetMapping
    suspend fun getAllIncidencias(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Any> {
        return incidenciaService.getAllIncidencias(page, size).mapBoth(
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

    @GetMapping("/export/csv")
    fun exportCsv(): ResponseEntity<ByteArray> {
        incidenciaCsvStorage.generateAndSaveCsv()

        val fileName = "incidencias_${LocalDate.now().toDefaultDateString()}.csv"
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

    @GetMapping("/export/pdf/{guid}")
    fun generateAndSavePdf(@PathVariable guid: String): ResponseEntity<String> {
        val fileName = "incidencia_${LocalDate.now().toDefaultDateString()}.pdf"
        incidenciaPdfStorage.generateAndSavePdf(guid)

        return ResponseEntity.ok("El PDF ha sido guardado exitosamente en la carpeta 'data' con el nombre $fileName")
    }
}