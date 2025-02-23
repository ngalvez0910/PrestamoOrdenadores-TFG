package org.example.prestamoordenadores.rest.alumnos.dto

data class AlumnoResponse (
    var guid : String,
    var name : String,
    var surname : String,
    var email : String,
    var grade : String,
    var createdDate : String,
    var updatedDate : String
)