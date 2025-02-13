package org.example.prestamoordenadores.rest.database.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class AlumnoEntity {
    @Id
    var id: Long = 0L
    var guid : String = ""
    var numeroEstudiante : String = ""
    var nombre: String = ""
    var apellidos: String = ""
    var email: String = ""
    var curso: String = ""
    var fotoCarnet: String = ""
    var userGuid: String = ""
    var enabled: Boolean = false
    var createdDate: String = ""
    var updatedDate: String = ""
}