package org.example.prestamoordenadores.rest.users.dto

data class UserResponse (
    var guid : String,
    var username: String,
    var email: String,
    var nombre: String,
    var apellidos: String,
    var curso: String,
    var tutor: String
)