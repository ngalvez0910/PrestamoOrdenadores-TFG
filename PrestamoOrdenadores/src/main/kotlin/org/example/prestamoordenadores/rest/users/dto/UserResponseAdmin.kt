package org.example.prestamoordenadores.rest.users.dto

import org.example.prestamoordenadores.rest.users.models.Role

class UserResponseAdmin {
    val guid: String = ""
    val username: String = ""
    val password: String = ""
    val roles: Role = Role.ALUMNO
    val enabled: Boolean = false
    var createdDate: String = ""
    var updatedDate: String = ""
    var lastLoginDate: String = ""
    var lastPasswordResetDate: String = ""
}