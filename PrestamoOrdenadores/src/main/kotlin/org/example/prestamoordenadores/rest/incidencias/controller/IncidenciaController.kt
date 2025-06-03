package org.example.prestamoordenadores.rest.incidencias.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError.IncidenciaNotFound
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError.IncidenciaValidationError
import org.example.prestamoordenadores.rest.incidencias.errors.IncidenciaError.UserNotFound
import org.example.prestamoordenadores.rest.incidencias.services.IncidenciaService
import org.example.prestamoordenadores.storage.pdf.IncidenciaPdfStorage
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

/**
 * Controlador REST para la gestión de incidencias.
 *
 * Este controlador expone endpoints para realizar operaciones CRUD y consultas relacionadas con las incidencias.
 * La mayoría de las operaciones requieren el rol 'ADMIN', aunque algunas de creación y consulta están
 * disponibles para 'ALUMNO' y 'PROFESOR'.
 *
 * @property incidenciaService El servicio de incidencias encargado de la lógica de negocio.
 * @property incidenciaPdfStorage El servicio para la generación y almacenamiento de PDFs de incidencias.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/incidencias")
@PreAuthorize("hasRole('ADMIN')") // Por defecto, todos los endpoints requieren rol ADMIN
class IncidenciaController
@Autowired constructor(
    private val incidenciaService: IncidenciaService,
    private val incidenciaPdfStorage: IncidenciaPdfStorage
) {
    /**
     * Obtiene todas las incidencias paginadas.
     *
     * @param page El número de página (por defecto 0).
     * @param size El tamaño de la página (por defecto 5).
     * @return [ResponseEntity] con una lista paginada de incidencias o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping
    fun getAllIncidencias(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "5") size: Int): ResponseEntity<Any> {
        return incidenciaService.getAllIncidencias(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    /**
     * Obtiene una incidencia por su GUID para una vista general (menos detallada).
     *
     * @param guid El GUID de la incidencia.
     * @return [ResponseEntity] con la incidencia encontrada o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @GetMapping("/{guid}")
    fun getIncidenciaByGuid(@PathVariable guid: String): ResponseEntity<Any> {
        return incidenciaService.getIncidenciaByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencia no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene una incidencia por su GUID para una vista administrativa (más detallada).
     *
     * @param guid El GUID de la incidencia.
     * @return [ResponseEntity] con la incidencia encontrada o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @GetMapping("/admin/{guid}")
    fun getIncidenciaByGuidAdmin(@PathVariable guid: String): ResponseEntity<Any> {
        return incidenciaService.getIncidenciaByGuidAdmin(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencia no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene una lista de incidencias por su estado.
     *
     * @param estado El estado de la incidencia.
     * @return [ResponseEntity] con una lista de incidencias en el estado especificado o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/estado/{estado}")
    fun getIncidenciaByEstado(@PathVariable estado: String): ResponseEntity<Any> {
        return incidenciaService.getIncidenciaByEstado(estado).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencias no encontradas")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene las incidencias asociadas a un usuario específico por su GUID.
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @param guid El GUID del usuario.
     * @return [ResponseEntity] con una lista de incidencias o un mensaje de error si el usuario no se encuentra.
     * @author Natalia González Álvarez
     */
    @GetMapping("/user/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun getIncidenciasByUserGuid(@PathVariable guid: String): ResponseEntity<Any> {
        return incidenciaService.getIncidenciasByUserGuid(guid).mapBoth(
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
     * Crea una nueva incidencia.
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @param incidencia La solicitud [IncidenciaCreateRequest] con los datos de la nueva incidencia.
     * @return [ResponseEntity] con la incidencia creada o un mensaje de error si los datos son inválidos.
     * @author Natalia González Álvarez
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun createIncidencia(@RequestBody incidencia: IncidenciaCreateRequest): ResponseEntity<Any> {
        return incidenciaService.createIncidencia(incidencia).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when (error) {
                    is IncidenciaValidationError -> ResponseEntity.status(400).body("Incidencia inválida")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Actualiza parcialmente una incidencia existente.
     *
     * @param guid El GUID de la incidencia a actualizar.
     * @param incidencia La solicitud [IncidenciaUpdateRequest] con los campos a actualizar.
     * @return [ResponseEntity] con la incidencia actualizada o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/{guid}")
    fun updateIncidencia(@PathVariable guid: String, @RequestBody incidencia: IncidenciaUpdateRequest): ResponseEntity<Any> {
        return incidenciaService.updateIncidencia(guid, incidencia).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencia no encontrada")
                    is IncidenciaValidationError -> ResponseEntity.status(403).body("Incidencia inválida")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Elimina lógicamente una incidencia (la marca como no disponible).
     *
     * @param guid El GUID de la incidencia a eliminar lógicamente.
     * @return [ResponseEntity] con la incidencia marcada como eliminada o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/delete/{guid}")
    fun deleteIncidencia(@PathVariable guid: String): ResponseEntity<Any> {
        return incidenciaService.deleteIncidenciaByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is IncidenciaNotFound -> ResponseEntity.status(404).body("Incidencia no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Genera y guarda un PDF de una incidencia específica.
     *
     * @param guid El GUID de la incidencia para la cual generar el PDF.
     * @return [ResponseEntity] con un mensaje de éxito indicando la ubicación del PDF.
     * @author Natalia González Álvarez
     */
    @GetMapping("/export/pdf/{guid}")
    fun generateAndSavePdf(@PathVariable guid: String): ResponseEntity<ByteArray> {
        val pdfBytes = incidenciaPdfStorage.generatePdf(guid)
        val fileName = "incidencia_${LocalDate.now().toDefaultDateString()}.pdf"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.contentDisposition = org.springframework.http.ContentDisposition.builder("attachment").filename(fileName).build()

        return ResponseEntity(pdfBytes, headers, HttpStatus.OK)
    }
}