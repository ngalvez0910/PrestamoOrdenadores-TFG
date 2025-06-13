package org.example.prestamoordenadores.rest.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Clase sellada (sealed class) base para excepciones relacionadas con la autenticación.
 * Todas las excepciones derivadas de [AuthException] resultarán en un estado HTTP 400 (Bad Request).
 *
 * @param message El mensaje descriptivo de la excepción.
 * @author Natalia González Álvarez
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
sealed class AuthException(message: String?) : RuntimeException(message) {
    /**
     * Excepción que indica que las credenciales de inicio de sesión son inválidas.
     * @param message El mensaje descriptivo (opcional).
     * @author Natalia González Álvarez
     */
    class AuthLoginInvalid(message: String?) : AuthException(message)

    /**
     * Excepción que indica que ya existe un usuario con el correo electrónico proporcionado.
     * @param message El mensaje descriptivo (opcional).
     * @author Natalia González Álvarez
     */
    class UserAuthEmailExist(message: String?) : AuthException(message)

    /**
     * Excepción que indica que las contraseñas proporcionadas no coinciden.
     * @param message El mensaje descriptivo (opcional).
     * @author Natalia González Álvarez
     */
    class UserDiferentePasswords(message: String?) : AuthException(message)
}