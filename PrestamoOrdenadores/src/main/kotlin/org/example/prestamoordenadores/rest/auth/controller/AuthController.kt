package org.example.prestamoordenadores.rest.auth.controller

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
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
import org.springframework.validation.ObjectError
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.MethodArgumentNotValidException
import java.util.function.Consumer

private val logger = logging()

@RestController
@RequestMapping("auth")
@Validated
class AuthenticationRestController @Autowired constructor(private val authenticationService: AuthenticationService) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: @Valid UserCreateRequest?): ResponseEntity<JwtAuthResponse> {
        logger.info{"Registrando usuario: $request"}
        return ResponseEntity.ok(authenticationService.signUp(request))
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody request: @Valid UserLoginRequest?): ResponseEntity<JwtAuthResponse> {
        logger.info{"Iniciando sesión de usuario: $request"}
        return ResponseEntity.ok(authenticationService.signIn(request))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        ConstraintViolationException::class
    )
    fun handleValidationExceptions(ex: Exception): Map<String, String?> {
        val errors: MutableMap<String, String?> = HashMap()

        if (ex is MethodArgumentNotValidException) {
            ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
                val fieldName = (error as FieldError).field
                val errorMessage = error.defaultMessage
                errors[fieldName] = errorMessage
            })
        } else if (ex is ConstraintViolationException) {
            ex.constraintViolations.forEach(Consumer { violation: ConstraintViolation<*> ->
                val fieldName = violation.propertyPath.toString()
                val errorMessage = violation.message
                errors[fieldName] = errorMessage
            })
        }

        return errors
    }
}