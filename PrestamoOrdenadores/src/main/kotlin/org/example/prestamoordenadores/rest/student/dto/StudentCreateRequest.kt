package org.example.prestamoordenadores.rest.student.dto

data class StudentCreateRequest (
    var studentNumber: String,
    var name: String,
    var surname: String,
    var email: String,
    var grade: String,
    var image: String,
    var username: String
)