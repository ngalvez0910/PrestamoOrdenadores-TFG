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

/**
 * Controlador REST para la autenticación de usuarios.
 *
 * Este controlador maneja las solicitudes de registro y inicio de sesión de usuarios,
 * y también incluye un manejador de excepciones para errores de validación.
 *
 * @property authenticationService El servicio de autenticación encargado de la lógica de negocio.
 * @author Natalia González Álvarez
 */
@RestController
@RequestMapping("auth")
@Validated
class AuthenticationRestController @Autowired constructor(private val authenticationService: AuthenticationService) {

    /**
     * Endpoint para el registro de un nuevo usuario.
     *
     * Valida la solicitud [UserCreateRequest] y delega el proceso de registro al servicio de autenticación.
     *
     * @param request Los datos de la solicitud para crear un nuevo usuario. Debe ser válido.
     * @return Una [ResponseEntity] que contiene un [JwtAuthResponse] con el token JWT si el registro es exitoso.
     * @author Natalia González Álvarez
     */
    @PostMapping("/signup")
    fun signUp(@RequestBody request: @Valid UserCreateRequest?): ResponseEntity<JwtAuthResponse> {
        logger.info{"Registrando usuario: $request"}
        return ResponseEntity.ok(authenticationService.signUp(request))
    }

    /**
     * Endpoint para el inicio de sesión de un usuario existente.
     *
     * Valida la solicitud [UserLoginRequest] y delega el proceso de inicio de sesión al servicio de autenticación.
     *
     * @param request Los datos de la solicitud para el inicio de sesión del usuario. Debe ser válido.
     * @return Una [ResponseEntity] que contiene un [JwtAuthResponse] con el token JWT si el inicio de sesión es exitoso.
     * @author Natalia González Álvarez
     */
    @PostMapping("/signin")
    fun signIn(@RequestBody request: @Valid UserLoginRequest?): ResponseEntity<JwtAuthResponse> {
        logger.info{"Iniciando sesión de usuario: $request"}
        return ResponseEntity.ok(authenticationService.signIn(request))
    }

    /**
     * Manejador global de excepciones para errores de validación.
     *
     * Captura [MethodArgumentNotValidException] (para validación de argumentos de método)
     * y [ConstraintViolationException] (para validación de restricciones en el nivel de servicio o repositorio).
     * Extrae los mensajes de error de validación y los devuelve en un mapa.
     *
     * @param ex La excepción de validación que ha sido lanzada.
     * @return Un mapa donde las claves son los nombres de los campos con errores y los valores son los mensajes de error.
     * @author Natalia González Álvarez
     */
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