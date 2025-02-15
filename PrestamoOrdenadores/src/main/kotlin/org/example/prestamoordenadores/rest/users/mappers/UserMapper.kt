package org.example.prestamoordenadores.rest.users.mappers

import org.example.prestamoordenadores.rest.users.dto.UserRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.cglib.core.Local
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserMapper {
    fun toUserResponse(user: User): UserResponse{
        return UserResponse(
            guid = user.guid,
            username = user.username,
            password = user.password,
            lastPasswordResetDate = user.lastPasswordResetDate.toString()
        )
    }

    fun toUserFromCreate(user: UserRequest): User {
        return User(
            username = user.username,
            password = user.password,
            roles = Role.ALUMNO,
            enabled = true,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now()
        )
    }
}