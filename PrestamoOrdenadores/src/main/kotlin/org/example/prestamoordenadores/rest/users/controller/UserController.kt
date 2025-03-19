package org.example.prestamoordenadores.rest.users.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.errors.UserError.UserAlreadyExists
import org.example.prestamoordenadores.rest.users.errors.UserError.UserNotFound
import org.example.prestamoordenadores.rest.users.services.UserService
import org.example.prestamoordenadores.storage.csv.UserCsvStorage
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
@RequestMapping("/users")
class UserController
@Autowired constructor(
    private val userService: UserService,
    private val userCsvStorage: UserCsvStorage
) {
    @GetMapping
    suspend fun getAllUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<Any> {
        return userService.getAllUsers(page, size).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body("Se ha producido un error en la solicitud") }
        )
    }

    @GetMapping("/{guid}")
    suspend fun getUserByGuid(@PathVariable guid: String) : ResponseEntity<Any> {
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
    suspend fun getUserByNombre(@PathVariable nombre: String) : ResponseEntity<Any> {
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
    suspend fun getUsersByGrade(@PathVariable curso: String) : ResponseEntity<Any> {
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

    @GetMapping("/email/{email}")
    suspend fun getUserByEmail(@PathVariable email: String) : ResponseEntity<Any> {
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
    suspend fun getUsersByTutor(@PathVariable tutor: String) : ResponseEntity<Any> {
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

    @PostMapping
    suspend fun createUser(@RequestBody user: UserCreateRequest): ResponseEntity<Any> {
        return userService.createUser(user).mapBoth(
            success = { ResponseEntity.status(201).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    is UserAlreadyExists -> ResponseEntity.badRequest().build()
                }
            }
        )
    }
    
    @PatchMapping("/{guid}")
    suspend fun resetPassword(@PathVariable guid: String, @RequestBody user: UserPasswordResetRequest) : ResponseEntity<Any> {
        return userService.resetPassword(guid, user).mapBoth(
            success = { ResponseEntity.status(200).body(it) },
            failure = { error ->
                when (error) {
                    is UserNotFound -> ResponseEntity.status(404).body("Usuario no encontrado")
                    else -> ResponseEntity.status(422).body("Se ha producido un error en la solicitud")
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    suspend fun deleteUserByGuid(@PathVariable guid: String) : ResponseEntity<Any> {
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

    @GetMapping("/export/csv")
    fun exportCsv(): ResponseEntity<ByteArray> {
        userCsvStorage.generateAndSaveCsv()

        val fileName = "usuarios_${LocalDate.now().toDefaultDateString()}.csv"
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