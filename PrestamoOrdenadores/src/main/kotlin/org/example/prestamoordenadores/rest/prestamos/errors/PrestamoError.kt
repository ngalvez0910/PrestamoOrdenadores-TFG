package org.example.prestamoordenadores.rest.prestamos.errors


sealed class PrestamoError(var message: String) {
    class PrestamoNotFound(message: String) : PrestamoError(message)
    class UserNotFound(message: String) : PrestamoError(message)
    class DispositivoNotFound(message: String) : PrestamoError(message)
}