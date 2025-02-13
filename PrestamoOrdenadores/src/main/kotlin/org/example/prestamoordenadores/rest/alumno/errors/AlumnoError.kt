package org.example.prestamoordenadores.rest.alumno.errors

sealed class AlumnoError(var message: String) {
    class AlumnoNotFound(message: String) : AlumnoError(message)
}