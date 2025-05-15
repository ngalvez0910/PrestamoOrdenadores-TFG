package org.example.prestamoordenadores.rest.users.errors

sealed class UserError (var message: String) {
    class UserNotFound(message: String) : UserError(message)
    class UserValidationError(message: String) : UserError(message)
    class DataBaseError(message: String) : UserError(message)
}