package org.example.prestamoordenadores.rest.users.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    var guid : String = "",

    @NotNull("Username no puede estar vacío")
    var username : String = "",

    @NotNull("Password no puede estar vacío")
    var password: String = "",

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
    constructor(guid: String, username: String, password: String, roles: Role, enabled: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, username, password, roles, true, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                LocalDateTime.now()
            )
}