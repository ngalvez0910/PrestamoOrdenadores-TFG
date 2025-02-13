package org.example.prestamoordenadores.rest.users.models

import java.time.LocalDateTime

class User {
    var id: Long = 0
    var guid : String = ""
    var username : String = ""
    var password: String = ""
    var roles: Role = Role.ALUMNO
    var enabled: Boolean = false
    var createdDate: LocalDateTime = LocalDateTime.now()
    var updatedDate: LocalDateTime = LocalDateTime.now()
    var lastLoginDate: LocalDateTime = LocalDateTime.now()
    var lastPasswordResetDate: LocalDateTime = LocalDateTime.now()
}