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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * Controlador REST para la gestión de sanciones.
 *
 * Este controlador expone endpoints para realizar operaciones CRUD y consultas relacionadas con las sanciones.
 * Todas las operaciones requieren el rol 'ADMIN' por defecto, aunque algunas de consulta
 * están disponibles para 'ALUMNO' y 'PROFESOR'.
 *
 * @property sancionService El servicio de sanciones encargado de la lógica de negocio.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/sanciones")
@PreAuthorize("hasRole('ADMIN')") // Por defecto, todos los endpoints requieren rol ADMIN
class SancionController
@Autowired constructor(
    private val sancionService: SancionService,
) {
    /**
     * Obtiene una lista paginada de todas las sanciones.
     *
     * @param page El número de página (por defecto 0).
     * @param size El tamaño de la página (por defecto 5).
     * @return [ResponseEntity] con una lista paginada de sanciones o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping
    fun getAllSanciones(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "5") size: Int): ResponseEntity<Any> {
        return sancionService.getAllSanciones(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    /**
     * Obtiene una sanción por su identificador único global (GUID).
     *
     * @param guid El GUID de la sanción.
     * @return [ResponseEntity] con la sanción encontrada o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @GetMapping("/{guid}")
    fun getSancionByGuid(@PathVariable guid: String): ResponseEntity<Any> {
        return sancionService.getSancionByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene una sanción por su identificador único global (GUID) con detalles administrativos.
     *
     * @param guid El GUID de la sanción.
     * @return [ResponseEntity] con la sanción encontrada (con detalles de admin) o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/admin/{guid}")
    fun getSancionByGuidAdmin(@PathVariable guid: String): ResponseEntity<Any> {
        return sancionService.getSancionByGuidAdmin(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene una lista de sanciones por su fecha de sanción.
     *
     * @param fecha La fecha de sanción a buscar.
     * @return [ResponseEntity] con una lista de sanciones o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/fecha/{fecha}")
    fun getSancionByFechaSancion(@PathVariable fecha: LocalDate): ResponseEntity<Any> {
        return sancionService.getByFecha(fecha).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    /**
     * Obtiene una lista de sanciones asociadas a un usuario específico por su GUID.
     * Permitido para roles 'ALUMNO', 'PROFESOR' y 'ADMIN'.
     *
     * @param guid El GUID del usuario.
     * @return [ResponseEntity] con una lista de sanciones o un mensaje de error si el usuario no se encuentra.
     * @author Natalia González Álvarez
     */
    @GetMapping("/user/{guid}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    fun getSancionesByUserGuid(@PathVariable guid: String): ResponseEntity<Any> {
        return sancionService.getSancionByUserGuid(guid).mapBoth(
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
     * Obtiene una lista de sanciones por su tipo.
     *
     * @param tipo El tipo de sanción a buscar.
     * @return [ResponseEntity] con una lista de sanciones o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/tipo/{tipo}")
    fun getSancionesByTipo(@PathVariable tipo: String): ResponseEntity<Any> {
        return sancionService.getByTipo(tipo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanciones no encontradas")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Actualiza una sanción existente.
     *
     * @param guid El GUID de la sanción a actualizar.
     * @param sancion La solicitud [SancionUpdateRequest] con los campos a actualizar.
     * @return [ResponseEntity] con la sanción actualizada o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/{guid}")
    fun updateSancion(@PathVariable guid: String, @RequestBody sancion: SancionUpdateRequest): ResponseEntity<Any> {
        return sancionService.updateSancion(guid, sancion).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    is SancionValidationError -> ResponseEntity.status(400).body("Sanción inválida")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Elimina lógicamente una sanción (la marca como no disponible).
     *
     * @param guid El GUID de la sanción a eliminar lógicamente.
     * @return [ResponseEntity] con la sanción marcada como eliminada o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/delete/{guid}")
    fun deleteSancion(@PathVariable guid: String): ResponseEntity<Any> {
        return sancionService.deleteSancionByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is SancionNotFound -> ResponseEntity.status(404).body("Sanción no encontrada")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }
}