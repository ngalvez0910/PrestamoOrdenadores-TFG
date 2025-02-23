package org.example.prestamoordenadores.rest.alumnos.dto

data class AlumnoCreateRequest (
    var studentNumber: String,
    var name: String,
    var surname: String,
    var email: String,
    var grade: String,
    var image: String,
    var username: String
)