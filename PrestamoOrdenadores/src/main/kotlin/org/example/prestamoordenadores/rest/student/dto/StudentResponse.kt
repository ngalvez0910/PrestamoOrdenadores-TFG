package org.example.prestamoordenadores.rest.student.dto

data class StudentResponse (
    var guid : String,
    var name : String,
    var surname : String,
    var email : String,
    var grade : String,
    var createdDate : String,
    var updatedDate : String
)