package org.example.prestamoordenadores.rest.users.errors

sealed class UserError (var message: String) {
    class UserNotFound(message: String) : UserError(message)
    class UserAlreadyExists(message: String) : UserError(message)
}