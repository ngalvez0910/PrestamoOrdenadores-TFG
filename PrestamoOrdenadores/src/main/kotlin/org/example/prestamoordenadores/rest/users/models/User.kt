package org.example.prestamoordenadores.rest.users.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "usuarios")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    var email: String = "",

    var password: String = "",

    @Enumerated(EnumType.STRING)
    var rol: Role = Role.ALUMNO,

    var numeroIdentificacion : String = "",

    var nombre: String = "",

    var apellidos: String = "",

    var curso: String? = "",

    var tutor: String?= "",

    var fotoCarnet: String= "",

    var avatar: String = "",

    var isActivo: Boolean = false,

    var lastLoginDate: LocalDateTime = LocalDateTime.now(),

    var lastPasswordResetDate: LocalDateTime = LocalDateTime.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
){
    constructor(guid: String, email: String, password: String, rol: Role, numeroIdentificacion: String, nombre: String, apellidos: String, curso: String, tutor: String, fotoCarnet: String, avatar: String, isActivo: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime, lastLoginDate: LocalDateTime, lastPasswordResetDate: LocalDateTime) :
            this(0, guid, email, password, rol, numeroIdentificacion, nombre, apellidos, curso, tutor, fotoCarnet, avatar, isActivo, createdDate, updatedDate, lastLoginDate, lastPasswordResetDate)
}