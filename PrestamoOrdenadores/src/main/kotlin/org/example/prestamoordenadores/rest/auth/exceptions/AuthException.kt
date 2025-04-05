package org.example.prestamoordenadores.rest.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
sealed class AuthException(message: String?) : RuntimeException(message) {
    class AuthLoginInvalid(message: String?) : AuthException(message)
    class UserAuthEmailExist(message: String?) : AuthException(message)
    class UserDiferentePasswords(message: String?) : AuthException(message)
}