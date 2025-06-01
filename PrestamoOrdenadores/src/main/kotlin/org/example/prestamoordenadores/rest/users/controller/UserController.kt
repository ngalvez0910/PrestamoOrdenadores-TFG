package org.example.prestamoordenadores.rest.users.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserUpdateRequest
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador REST para la gestión de usuarios.
 * Define los endpoints para las operaciones relacionadas con los usuarios,
 * como obtener, actualizar y eliminar usuarios.
 * Todas las operaciones en este controlador requieren el rol 'ADMIN' por defecto,
 * a menos que se especifique lo contrario con [@PreAuthorize].
 *
 * @property userService Servicio encargado de la lógica de negocio de los usuarios.
 *
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
class UserController
@Autowired constructor(
    private val userService: UserService,
) {
    /**
     * Obtiene una lista paginada de todos los usuarios.
     * Requiere el rol 'ADMIN'.
     *
     * @param page Número de página a recuperar (por defecto 0).
     * @param size Tamaño de la página (por defecto 5).
     * @return [ResponseEntity] con la lista paginada de usuarios si es exitoso, o un mensaje de error.
     */
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

    /**
     * Obtiene un usuario por su identificador único (GUID).
     * Requiere los roles 'ALUMNO', 'PROFESOR' o 'ADMIN'.
     *
     * @param guid El GUID del usuario a buscar.
     * @return [ResponseEntity] con los detalles del usuario si es encontrado, o un mensaje de error 404 si no existe.
     */
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

    /**
     * Obtiene un usuario por su nombre.
     * Requiere el rol 'ADMIN'.
     *
     * @param nombre El nombre del usuario a buscar.
     * @return [ResponseEntity] con los detalles del usuario si es encontrado, o un mensaje de error 404 si no existe.
     */
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

    /**
     * Obtiene una lista de usuarios filtrados por su curso.
     * Requiere el rol 'ADMIN'.
     *
     * @param curso El curso de los usuarios a buscar.
     * @return [ResponseEntity] con la lista de usuarios si es exitoso, o un mensaje de error 404 si no se encuentran usuarios.
     */
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

    /**
     * Obtiene un usuario por su dirección de correo electrónico.
     * Requiere los roles 'ALUMNO', 'PROFESOR' o 'ADMIN'.
     *
     * @param email La dirección de correo electrónico del usuario a buscar.
     * @return [ResponseEntity] con los detalles del usuario si es encontrado, o un mensaje de error 404 si no existe.
     */
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

    /**
     * Obtiene una lista de usuarios filtrados por el nombre de su tutor.
     * Requiere el rol 'ADMIN'.
     *
     * @param tutor El nombre del tutor de los usuarios a buscar.
     * @return [ResponseEntity] con la lista de usuarios si es exitoso, o un mensaje de error 404 si no se encuentran usuarios.
     */
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

    /**
     * Actualiza el avatar de un usuario.
     * Requiere los roles 'ADMIN', 'ALUMNO' o 'PROFESOR'.
     *
     * @param guid El GUID del usuario cuyo avatar se va a actualizar.
     * @param user DTO con la nueva URL del avatar.
     * @return [ResponseEntity] con los detalles del usuario actualizado si es exitoso,
     * o un mensaje de error si el usuario no es encontrado o los datos son inválidos.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR')")
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

    /**
     * Restablece la contraseña de un usuario.
     * Requiere los roles 'ADMIN', 'ALUMNO' o 'PROFESOR'.
     *
     * @param guid El GUID del usuario cuya contraseña se va a restablecer.
     * @param user DTO con la nueva contraseña.
     * @return [ResponseEntity] con los detalles del usuario actualizado si es exitoso,
     * o un mensaje de error si el usuario no es encontrado o los datos son inválidos.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR')")
    @PatchMapping("/password/{guid}")
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

    /**
     * Marca un usuario como eliminado (baja lógica) por su GUID.
     * Requiere el rol 'ADMIN'.
     *
     * @param guid El GUID del usuario a eliminar.
     * @return [ResponseEntity] con estado 200 si la eliminación es exitosa,
     * o un mensaje de error 404 si el usuario no es encontrado.
     */
    @PatchMapping("/delete/{guid}")
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

    /**
     * Obtiene un usuario por su GUID, con detalles adicionales para la vista de administrador.
     * Requiere el rol 'ADMIN'.
     *
     * @param guid El GUID del usuario a buscar.
     * @return [ResponseEntity] con los detalles del usuario si es encontrado, o un mensaje de error 404 si no existe.
     */
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

    /**
     * Actualiza la información de un usuario.
     * Requiere el rol 'ADMIN'.
     *
     * @param guid El GUID del usuario a actualizar.
     * @param user DTO con los datos de actualización del usuario.
     * @return [ResponseEntity] con los detalles del usuario actualizado si es exitoso,
     * o un mensaje de error si el usuario no es encontrado.
     */
    @PutMapping("/{guid}")
    fun updateUser(@PathVariable guid: String, @RequestBody user: UserUpdateRequest) : ResponseEntity<Any> {
        return userService.updateUser(guid, user).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    /**
     * Realiza el "derecho al olvido" para un usuario, eliminando sus datos sensibles.
     * Requiere el rol 'ADMIN'.
     *
     * @param userGuid El GUID del usuario para el cual aplicar el derecho al olvido.
     * @return [ResponseEntity] con estado 200 si la operación es exitosa,
     * o un mensaje de error 404 si el usuario no es encontrado.
     */
    @DeleteMapping("/derechoOlvido/{userGuid}")
    fun derechoAlOlvido(@PathVariable userGuid: String): ResponseEntity<Any> {
        return userService.derechoAlOlvido(userGuid).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }
}