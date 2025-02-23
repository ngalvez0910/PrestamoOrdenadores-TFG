package org.example.prestamoordenadores.rest.users.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "usuarios")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    @NotNull("Username no puede estar vacío")
    var username : String = "",

    @NotNull("Password no puede estar vacío")
    var password: String = "",

    @Enumerated(EnumType.STRING)
    @NotNull("Rol no puede estar vacío")
    var rol: Role = Role.ALUMNO,

    @NotNull("Email no puede ser null")
    var email: String = "",

    @NotNull("Numero identificacion no puede ser null")
    var numeroIdentificacion : String = "",

    @NotNull("Nombre no puede ser null")
    var nombre: String = "",

    @NotNull("Apellidos no puede ser null")
    var apellidos: String = "",

    @NotNull("Curso no puede ser null")
    var curso: String = "",

    @NotNull("Tutor no puede ser null")
    var tutor: String = "",

    @NotNull("Foto carnet no puede ser null")
    var fotoCarnet: String = "",

    @NotNull("Avatar no puede ser null")
    var avatar: String = "",

    var isActivo: Boolean = false,

    var lastLoginDate: LocalDateTime = LocalDateTime.now(),

    var lastPasswordResetDate: LocalDateTime = LocalDateTime.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
){
    constructor(guid: String, username: String, password: String, rol: Role, email: String, numeroIdentificacion: String, nombre: String, apellidos: String, curso: String, tutor: String, fotoCarnet: String, avatar: String, isActivo: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime, lastLoginDate: LocalDateTime, lastPasswordResetDate: LocalDateTime) :
            this(0, guid, username, password, rol, email, numeroIdentificacion, nombre, apellidos, curso, tutor, fotoCarnet, avatar, isActivo, createdDate, updatedDate, lastLoginDate, lastPasswordResetDate)
}