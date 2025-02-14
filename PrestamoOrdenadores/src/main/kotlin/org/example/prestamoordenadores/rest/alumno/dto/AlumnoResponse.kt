package org.example.prestamoordenadores.rest.alumno.dto

data class AlumnoResponse (
    var guid : String,
    var nombre : String,
    var apellidos : String,
    var email : String,
    var curso : String,
    var createdDate : String,
    var updatedDate : String
)