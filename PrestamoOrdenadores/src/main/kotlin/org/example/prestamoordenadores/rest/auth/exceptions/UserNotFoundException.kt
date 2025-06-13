package org.example.prestamoordenadores.rest.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Excepción que se lanza cuando no se encuentra un usuario.
 *
 * Esta excepción resulta en un estado HTTP 404 (Not Found) cuando es lanzada
 * desde un controlador Spring.
 *
 * @param message El mensaje descriptivo de la excepción.
 * @author Natalia González Álvarez
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(message: String) : RuntimeException(message) {
}