package org.example.prestamoordenadores.rest.users.mappers

import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserMapper {
    fun toUserResponse(user: User): UserResponse{
        return UserResponse(
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso ?:"",
            tutor = user.tutor ?:""
        )
    }

    fun toUserResponseAdmin(user: User): UserResponseAdmin{
        return UserResponseAdmin(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso ?:"",
            tutor = user.tutor ?:"",
            rol = user.rol,
            isActivo = user.isActivo,
            createdDate = user.createdDate.toDefaultDateTimeString(),
            updatedDate = user.updatedDate.toDefaultDateTimeString(),
            lastLoginDate = user.lastLoginDate.toDefaultDateTimeString(),
            lastPasswordResetDate = user.lastPasswordResetDate.toDefaultDateTimeString(),
        )
    }

    fun toUserFromCreate(user: UserCreateRequest): User {
        return User(
            numeroIdentificacion = user.numeroIdentificacion,
            nombre = user.nombre,
            apellidos = user.apellidos,
            email = user.email,
            curso = user.curso,
            tutor = user.tutor,
            fotoCarnet = user.fotoCarnet,
            password = user.password,
            rol = Role.ALUMNO,
            isActivo = true,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now()
        )
    }

    fun toUserFromResponseAdmin(userResponse: UserResponseAdmin?): User {
        return User(
            id = 0L,
            guid = userResponse?.guid ?: "",
            email = userResponse?.email ?: "",
            password = "",
            rol = Role.valueOf((userResponse?.rol ?: "ALUMNO").toString()),
            numeroIdentificacion = userResponse?.numeroIdentificacion ?: "",
            nombre = userResponse?.nombre ?: "",
            apellidos = userResponse?.apellidos ?: "",
            curso = userResponse?.curso ?: "",
            tutor = userResponse?.tutor ?: "",
            fotoCarnet = "",
            avatar = "",
            isActivo = userResponse?.isActivo == true,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toUserResponseList(users: List<User?>): List<UserResponse> {
        return users.map { toUserResponse(it!!) }
    }

    fun toUserResponseListAdmin(users: List<User?>): List<UserResponseAdmin> {
        return users.map { toUserResponseAdmin(it!!) }
    }
}