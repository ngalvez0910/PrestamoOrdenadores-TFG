package org.example.prestamoordenadores.rest.auth.controller

import jakarta.validation.Valid
import org.example.prestamoordenadores.rest.auth.dto.JwtAuthResponse
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.auth.services.authentication.AuthenticationService
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.MethodArgumentNotValidException

private val log = logging()

@RestController
@RequestMapping("auth")
class AuthController {
    private var authenticationService: AuthenticationService? = null

    @Autowired
    fun AuthenticationRestController(authenticationService: AuthenticationService?) {
        this.authenticationService = authenticationService
    }

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request: UserCreateRequest?): ResponseEntity<JwtAuthResponse?> {
        log.info { "Registrando usuario: {}$request" }
        return ResponseEntity.ok<JwtAuthResponse?>(authenticationService!!.signUp(request))
    }

    @PostMapping("/signin")
    fun signIn(@Valid @RequestBody request: UserLoginRequest?): ResponseEntity<JwtAuthResponse?> {
        log.info { "Iniciando sesión de usuario: {}$request" }
        return ResponseEntity.ok<JwtAuthResponse?>(authenticationService!!.signIn(request))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): MutableMap<String?, String?> {
        val errors: MutableMap<String?, String?> = HashMap<String?, String?>()
        ex.getBindingResult().getAllErrors().forEach({ error ->
            val fieldName = (error as FieldError).getField()
            val errorMessage = error.getDefaultMessage()
            errors.put(fieldName, errorMessage)
        })
        return errors
    }
}