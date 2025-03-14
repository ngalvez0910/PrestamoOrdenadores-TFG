package org.example.prestamoordenadores.rest.dispositivos.errors

sealed class DispositivoError(var message: String) {
    class DispositivoNotFound(message: String) : DispositivoError(message)
}