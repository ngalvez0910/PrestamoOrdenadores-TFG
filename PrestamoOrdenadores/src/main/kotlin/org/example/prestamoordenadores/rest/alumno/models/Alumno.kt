package org.example.prestamoordenadores.rest.alumno.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.users.models.User
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "alumnos")
class Alumno(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,
    var guid : String = "",

    @NotNull("Numero de Estudiante no puede estar vacío")
    var numeroEstudiante : String = "",

    @NotNull("Nombre no puede estar vacío")
    var nombre: String = "",

    @NotNull("Apellidos no puede estar vacío")
    var apellidos: String = "",

    @NotNull("Email no puede estar vacío")
    var email: String = "",

    @NotNull("Curso no puede estar vacío")
    var curso: String = "",

    @NotNull("Foto del carnet no puede estar vacío")
    var fotoCarnet: String = "",

    @OneToOne
    @JoinColumn(name = "user_guid")
    var user: User = User(),
    var enabled: Boolean = true,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now()
){
    constructor(guid: String, numeroEstudiante: String, nombre: String, apellidos: String, email: String, curso: String, fotoCarnet: String, user: User, enabled: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, numeroEstudiante, nombre, apellidos, email, curso, fotoCarnet, user, true, LocalDateTime.now(),
                LocalDateTime.now()
            )
}