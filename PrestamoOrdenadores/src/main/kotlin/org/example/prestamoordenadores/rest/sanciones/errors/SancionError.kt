package org.example.prestamoordenadores.rest.sanciones.errors

sealed class SancionError(var message: String) {
    class SancionNotFound(message: String) : SancionError(message)
    class UserNotFound(message: String) : SancionError(message)
    class SancionValidationError(message: String) : SancionError(message)
}