package org.example.prestamoordenadores.rest.users.dto

data class UserRequest (
    var username : String,
    var password : String,
    var confirmPassword : String
)