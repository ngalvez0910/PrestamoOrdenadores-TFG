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
@Table(name = "users")
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
    var roles: Role = Role.ALUMNO,

    var enabled: Boolean = false,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
    var lastLoginDate: LocalDateTime = LocalDateTime.now(),
    var lastPasswordResetDate: LocalDateTime = LocalDateTime.now()
){
    constructor(guid: String, username: String, password: String, roles: Role, enabled: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime, lastLoginDate: LocalDateTime, lastPasswordResetDate: LocalDateTime) :
            this(0, guid, username, password, roles, enabled, createdDate, updatedDate, lastLoginDate, lastPasswordResetDate)
}