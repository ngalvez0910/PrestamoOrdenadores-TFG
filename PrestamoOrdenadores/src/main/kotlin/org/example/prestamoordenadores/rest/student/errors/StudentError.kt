package org.example.prestamoordenadores.rest.student.errors

sealed class StudentError(var message: String) {
    class StudentNotFound(message: String) : StudentError(message)
    class UserNotFound(message: String) : StudentError(message)
    class StudentAlreadyExists(message: String) : StudentError(message)
}