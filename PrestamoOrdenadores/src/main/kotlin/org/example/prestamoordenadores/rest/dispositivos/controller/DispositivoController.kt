package org.example.prestamoordenadores.rest.dispositivos.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoUpdateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError.DispositivoNotFound
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError.DispositivoValidationError
import org.example.prestamoordenadores.rest.dispositivos.services.DispositivoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Controlador REST para la gestión de dispositivos.
 *
 * Este controlador expone endpoints para realizar operaciones CRUD y consultas relacionadas con los dispositivos.
 * Todas las operaciones requieren que el usuario tenga el rol 'ADMIN'.
 *
 * @property dispositivoService El servicio de dispositivos encargado de la lógica de negocio.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/dispositivos")
@PreAuthorize("hasRole('ADMIN')")
class DispositivoController
@Autowired constructor(
    private val dispositivoService: DispositivoService,
) {
    /**
     * Obtiene todos los dispositivos paginados.
     *
     * @param page El número de página (por defecto 0).
     * @param size El tamaño de la página (por defecto 5).
     * @return [ResponseEntity] con una lista de dispositivos o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping
    fun getAllDispositivos(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "5") size: Int): ResponseEntity<Any> {
        return dispositivoService.getAllDispositivos(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    /**
     * Obtiene un dispositivo por su GUID.
     *
     * @param guid El GUID del dispositivo.
     * @return [ResponseEntity] con el dispositivo encontrado o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @GetMapping("/{guid}")
    fun getDispositivoByGuid(@PathVariable guid: String): ResponseEntity<Any> {
        return dispositivoService.getDispositivoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene un dispositivo por su número de serie.
     *
     * @param numeroSerie El número de serie del dispositivo.
     * @return [ResponseEntity] con el dispositivo encontrado o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @GetMapping("/numeroSerie/{numeroSerie}")
    fun getDispositivoByNumeroSerie(@PathVariable numeroSerie: String): ResponseEntity<Any> {
        return dispositivoService.getDispositivoByNumeroSerie(numeroSerie).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene dispositivos por su estado.
     *
     * @param estado El estado del dispositivo.
     * @return [ResponseEntity] con una lista de dispositivos en el estado especificado o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/estado/{estado}")
    fun getDispositivoByEstado(@PathVariable estado: String): ResponseEntity<Any> {
        return dispositivoService.getDispositivoByEstado(estado).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivos no encontrados")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Crea un nuevo dispositivo.
     *
     * @param dispositivo La solicitud [DispositivoCreateRequest] con los datos del nuevo dispositivo.
     * @return [ResponseEntity] con el dispositivo creado o un mensaje de error si los datos son inválidos o hay un problema.
     * @author Natalia González Álvarez
     */
    @PostMapping
    fun createDispositivo(@RequestBody dispositivo: DispositivoCreateRequest): ResponseEntity<Any> {
        return dispositivoService.createDispositivo(dispositivo).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when (error) {
                    is DispositivoValidationError -> ResponseEntity.status(400).body("Dispositivo inválido")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Actualiza parcialmente un dispositivo existente.
     *
     * @param guid El GUID del dispositivo a actualizar.
     * @param dispositivo La solicitud [DispositivoUpdateRequest] con los campos a actualizar.
     * @return [ResponseEntity] con el dispositivo actualizado o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/{guid}")
    fun updateDispositivo(@PathVariable guid: String, @RequestBody dispositivo: DispositivoUpdateRequest): ResponseEntity<Any> {
        return dispositivoService.updateDispositivo(guid, dispositivo).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Elimina lógicamente un dispositivo (marcarlo como no disponible).
     *
     * @param guid El GUID del dispositivo a eliminar lógicamente.
     * @return [ResponseEntity] con el dispositivo marcado como no disponible o un mensaje de error si no se encuentra o hay un problema.
     * @author Natalia González Álvarez
     */
    @PatchMapping("/delete/{guid}")
    fun deleteDispositivo(@PathVariable guid: String): ResponseEntity<Any> {
        return dispositivoService.deleteDispositivoByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is DispositivoNotFound -> ResponseEntity.status(404).body("Dispositivo no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Obtiene el stock actual de dispositivos disponibles.
     *
     * @return [ResponseEntity] con el stock de dispositivos o un mensaje de error.
     * @author Natalia González Álvarez
     */
    @GetMapping("/stock")
    fun getStock(): ResponseEntity<Any> {
        return dispositivoService.getStock().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }
}