package org.example.prestamoordenadores.rest.users.dto

class UserPasswordResetRequest {
    var oldPassword: String = ""
    var newPassword: String = ""
    var confirmPassword: String = ""
}