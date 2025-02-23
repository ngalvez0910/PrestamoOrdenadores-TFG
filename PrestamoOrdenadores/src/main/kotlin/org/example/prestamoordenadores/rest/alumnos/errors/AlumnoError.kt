package org.example.prestamoordenadores.rest.alumnos.errors

sealed class AlumnoError(var message: String) {
    class AlumnoNotFound(message: String) : AlumnoError(message)
    class UserNotFound(message: String) : AlumnoError(message)
    class AlumnoAlreadyExists(message: String) : AlumnoError(message)
}