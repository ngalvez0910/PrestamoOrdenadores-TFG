package org.example.prestamoordenadores.config.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class TokenInvalidoException(message: String) : RuntimeException(message)