package org.example.prestamoordenadores.rest.alumno.models

import java.time.LocalDateTime

class Alumno {
    var id: Long = 0
    var guid : String = ""
    var numeroEstudiante : String = ""
    var nombre: String = ""
    var apellidos: String = ""
    var email: String = ""
    var curso: String = ""
    var fotoCarnet: String = ""
    var enabled: Boolean = false
    var createdDate: LocalDateTime = LocalDateTime.now()
    var updatedDate: LocalDateTime = LocalDateTime.now()
}