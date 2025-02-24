package org.example.prestamoordenadores.rest.prestamos.errors

sealed class PrestamoError(var message: String) {
    class PrestamoNotFound(message: String) : PrestamoError(message)
}