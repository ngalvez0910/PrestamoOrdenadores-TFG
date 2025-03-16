package org.example.prestamoordenadores.rest.sanciones.errors

sealed class SancionError(var message: String) {
    class SancionNotFound(message: String) : SancionError(message)
}