package org.example.prestamoordenadores.rest.dispositivos.errors

sealed class DispositivoError(var message: String) {
    class DispositivoNotFound(message: String) : DispositivoError(message)
    class DispositivoAlreadyExists(message: String) : DispositivoError(message)
    class DispositivoValidationError(message: String) : DispositivoError(message)
    class IncidenciaNotFound(message: String) : DispositivoError(message)
}