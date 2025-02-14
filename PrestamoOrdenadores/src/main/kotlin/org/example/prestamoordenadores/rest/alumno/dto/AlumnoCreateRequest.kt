package org.example.prestamoordenadores.rest.alumno.dto

data class AlumnoCreateRequest (
    var numeroEstudiante: String,
    var nombre: String,
    var apellidos: String,
    var email: String,
    var curso: String,
    var fotoCarnet: String,
    var username: String
)