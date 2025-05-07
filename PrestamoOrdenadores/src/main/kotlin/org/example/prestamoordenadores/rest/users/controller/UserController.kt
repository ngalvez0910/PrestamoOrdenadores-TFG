package org.example.prestamoordenadores.rest.users.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserRoleUpdateRequest
import org.example.prestamoordenadores.rest.users.errors.UserError.UserNotFound
import org.example.prestamoordenadores.rest.users.errors.UserError.UserValidationError
import org.example.prestamoordenadores.rest.users.services.UserService
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

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
class UserController
@Autowired constructor(
    private val userService: UserService,
) {
    @GetMapping
    fun getAllUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Any> {
        return userService.getAllUsers(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR')")
    @GetMapping("/{guid}")
    fun getUserByGuid(@PathVariable guid: String) : ResponseEntity<Any> {
        return userService.getUserByGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/nombre/{nombre}")
    fun getUserByNombre(@PathVariable nombre: String) : ResponseEntity<Any> {
        return userService.getByNombre(nombre).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/curso/{curso}")
    fun getUsersByGrade(@PathVariable curso: String) : ResponseEntity<Any> {
        return userService.getByCurso(curso).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String) : ResponseEntity<Any> {
        return userService.getByEmail(email).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/tutor/{tutor}")
    fun getUsersByTutor(@PathVariable tutor: String) : ResponseEntity<Any> {
        return userService.getByTutor(tutor).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuarios no encontrados")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR')")
    @PatchMapping("/avatar/{guid}")
    fun updateAvatar(@PathVariable guid: String, @RequestBody user: UserAvatarUpdateRequest) : ResponseEntity<Any> {
        return userService.updateAvatar(guid, user).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    is UserValidationError -> ResponseEntity.status(403).body("Usuario inválido")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR')")
    @PatchMapping("/password//{guid}")
    fun resetPassword(@PathVariable guid: String, @RequestBody user: UserPasswordResetRequest) : ResponseEntity<Any> {
        return userService.resetPassword(guid, user).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    is UserValidationError -> ResponseEntity.status(403).body("Usuario inválido")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deleteUserByGuid(@PathVariable guid: String) : ResponseEntity<Any> {
        return userService.deleteUserByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).build() },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @GetMapping("/admin/{guid}")
    fun getUserByGuidAdmin(@PathVariable guid: String) : ResponseEntity<Any> {
        return userService.getUserByGuidAdmin(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @PatchMapping("/rol/{guid}")
    fun updateRol(@PathVariable guid: String, @RequestBody user: UserRoleUpdateRequest) : ResponseEntity<Any> {
        return userService.updateRole(guid, user).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    is UserValidationError -> ResponseEntity.status(403).body("Usuario inválido")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }
}