package org.example.prestamoordenadores.rest.users.controller

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.errors.UserError.UserAlreadyExists
import org.example.prestamoordenadores.rest.users.errors.UserError.UserNotFound
import org.example.prestamoordenadores.rest.users.models.User
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
    fun getAllUsers() : Result<List<User>, UserError> = userService.getAllUsers()

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

    @GetMapping("/{username}")
    fun getUserByUsername(@PathVariable username: String) : ResponseEntity<UserResponse?> {
        return userService.getByUsername(username).mapBoth(
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
    fun createUser(@RequestBody user: UserRequest): ResponseEntity<UserResponse> {
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
    fun resetPassword(@PathVariable guid: String, @RequestBody user: UserPasswordResetRequest) : Result<UserResponse?, UserError> = userService.resetPassword(guid, user)

    @DeleteMapping("/{guid}")
    fun deleteUserByGuid(@PathVariable guid: String) : Result<UserResponse?, UserError> = userService.deleteUserByGuid(guid)
}