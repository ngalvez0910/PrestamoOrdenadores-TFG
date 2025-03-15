package org.example.prestamoordenadores.rest.users.dto

import org.jetbrains.annotations.NotNull

data class UserCreateRequest (
    @NotNull("Numero identificacion no puede ser null")
    var numeroIdentificacion: String,
    @NotNull("Nombre no puede ser null")
    var nombre: String,
    @NotNull("Apellidos no puede ser null")
    var apellidos: String,
    @NotNull("Email no puede ser null")
    var email: String,
    var curso: String,
    var tutor: String,
    @NotNull("Foto carnet no puede ser null")
    var fotoCarnet: String,
    @NotNull("Password no puede estar vacío")
    var password : String,
    @NotNull("Confirm password no puede estar vacío")
    var confirmPassword : String
)