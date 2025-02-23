package org.example.prestamoordenadores.rest.users.dto

data class UserCreateRequest (
    var numeroIdentificacion: String,
    var nombre: String,
    var apellidos: String,
    var email: String,
    var curso: String,
    var tutor: String,
    var fotoCarnet: String,
    var username : String,
    var password : String,
    var confirmPassword : String
)