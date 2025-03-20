package org.example.prestamoordenadores.rest.incidencias.errors

sealed class IncidenciaError(var message: String) {
    class IncidenciaNotFound(message: String) : IncidenciaError(message)
    class UserNotFound(message: String) : IncidenciaError(message)
    class IncidenciaValidationError(message: String) : IncidenciaError(message)
}