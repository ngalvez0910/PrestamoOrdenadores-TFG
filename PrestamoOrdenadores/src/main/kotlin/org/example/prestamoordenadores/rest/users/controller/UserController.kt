package org.example.prestamoordenadores.rest.users.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.errors.UserError.UserAlreadyExists
import org.example.prestamoordenadores.rest.users.errors.UserError.UserNotFound
import org.example.prestamoordenadores.rest.users.services.UserService
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
@RequestMapping("/users")
class UserController
@Autowired constructor(
    private val userService: UserService
) {
    @GetMapping
    fun getAllUsers() : ResponseEntity<List<UserResponse>> {
        return userService.getAllUsers().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @GetMapping("/{guid}")
    fun getUserByGuid(@PathVariable guid: String) : ResponseEntity<UserResponse?> {
        return userService.getUserByGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/nombre/{nombre}")
    fun getStudentByNombre(@PathVariable nombre: String) : ResponseEntity<UserResponse?> {
        return userService.getByNombre(nombre).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/curso/{curso}")
    fun getStudentsByGrade(@PathVariable curso: String) : ResponseEntity<List<UserResponse?>> {
        return userService.getByCurso(curso).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/email/{email}")
    fun getStudentByEmail(@PathVariable email: String) : ResponseEntity<UserResponse?> {
        return userService.getByEmail(email).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @PostMapping
    fun createUser(@RequestBody user: UserCreateRequest): ResponseEntity<UserResponse> {
        return userService.createUser(user).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    is UserAlreadyExists -> ResponseEntity.badRequest().build()
                }
            }
        )
    }
    
    @PatchMapping("/{guid}")
    fun resetPassword(@PathVariable guid: String, @RequestBody user: UserPasswordResetRequest) : ResponseEntity<UserResponse?> {
        return userService.resetPassword(guid, user).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deleteUserByGuid(@PathVariable guid: String) : ResponseEntity<UserResponse?> {
        return userService.deleteUserByGuid(guid).mapBoth(
            success = { ResponseEntity.status(200).build() },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }
}