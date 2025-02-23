package org.example.prestamoordenadores.rest.alumnos.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.generators.generateGuid
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
    var guid : String = generateGuid(),

    @NotNull("Numero estudiante no puede ser null")
    var numeroEstudiante : String = "",

    @NotNull("Nombre no puede ser null")
    var nombre: String = "",

    @NotNull("Apellidos no puede ser null")
    var apellidos: String = "",

    @NotNull("Email no puede ser null")
    var email: String = "",

    @NotNull("Curso no puede ser null")
    var curso: String = "",

    @NotNull("Foto carnet no puede ser null")
    var fotoCarnet: String = "",

    @NotNull("Avatar no puede ser null")
    var avatar: String = "",

    @NotNull("Tutor no puede ser null")
    var tutor: String = "",

    @OneToOne
    @JoinColumn(name = "user_guid")
    var user: User = User(),

    @OneToMany(mappedBy = "solicitante_guid")
    @JoinColumn(name = "prestamo_guid")
    var prestamosGuid: MutableList<Prestamo>? = mutableListOf(),

    @OneToMany(mappedBy = "solicitante")
    var incidenciasGuid: MutableList<Incidencia>? = mutableListOf(),

    var isActivo: Boolean = true,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now()
){
    constructor(guid: String, numeroEstudiante: String, nombre: String, apellidos: String, email: String, curso: String, fotoCarnet: String, avatar: String, tutor: String, user: User, prestamosGuid:List<Prestamo>, incidenciasGuid:List<Incidencia>, isActivo: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, numeroEstudiante, nombre, apellidos, email, curso, fotoCarnet, avatar, tutor, user, prestamosGuid, incidenciasGuid, isActivo, createdDate, updatedDate
            )
}