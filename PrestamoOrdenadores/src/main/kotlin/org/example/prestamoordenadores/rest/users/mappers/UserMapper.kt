package org.example.prestamoordenadores.rest.users.mappers

import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Mapper para convertir entre las entidades de usuario y los DTOs relacionados.
 *
 * Proporciona funciones para transformar objetos `User` a diferentes respuestas DTO y viceversa.
 *
 * @author Natalia González Álvarez
 */
@Component
class UserMapper {

    /**
     * Convierte un objeto `User` a un DTO `UserResponse`.
     *
     * @param user Entidad de usuario a convertir.
     * @return DTO con información básica del usuario.
     */
    fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso ?: "",
            tutor = user.tutor ?: "",
            avatar = user.avatar,
        )
    }

    /**
     * Convierte un objeto `User` a un DTO `UserResponseAdmin` con información detallada para administración.
     *
     * @param user Entidad de usuario a convertir.
     * @return DTO con información extendida del usuario.
     */
    fun toUserResponseAdmin(user: User): UserResponseAdmin {
        return UserResponseAdmin(
            numeroIdentificacion = user.numeroIdentificacion,
            guid = user.guid,
            email = user.email,
            nombre = user.nombre,
            apellidos = user.apellidos,
            curso = user.curso ?: "",
            tutor = user.tutor ?: "",
            rol = user.rol,
            isActivo = user.isActivo,
            createdDate = user.createdDate.toDefaultDateTimeString(),
            updatedDate = user.updatedDate.toDefaultDateTimeString(),
            lastLoginDate = user.lastLoginDate.toDefaultDateTimeString(),
            lastPasswordResetDate = user.lastPasswordResetDate.toDefaultDateTimeString(),
            isDeleted = user.isDeleted,
            isOlvidado = user.isOlvidado
        )
    }

    /**
     * Convierte un DTO `UserCreateRequest` a una entidad `User`.
     *
     * @param user DTO con la información para crear un usuario.
     * @return Entidad de usuario creada a partir del DTO.
     */
    fun toUserFromCreate(user: UserCreateRequest): User {
        return User(
            numeroIdentificacion = user.numeroIdentificacion!!,
            nombre = user.nombre!!,
            apellidos = user.apellidos!!,
            email = user.email!!,
            curso = user.curso,
            tutor = user.tutor,
            campoPassword = user.password!!,
            rol = Role.ALUMNO,
            isActivo = true,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now()
        )
    }

    /**
     * Convierte una lista de entidades `User` a una lista de DTOs `UserResponse`.
     *
     * @param users Lista de usuarios (puede contener nulls).
     * @return Lista de DTOs con información básica del usuario.
     */
    fun toUserResponseList(users: List<User?>): List<UserResponse> {
        return users.map { toUserResponse(it!!) }
    }

    /**
     * Convierte una lista de entidades `User` a una lista de DTOs `UserResponseAdmin`.
     *
     * @param users Lista de usuarios (puede contener nulls).
     * @return Lista de DTOs con información detallada para administración.
     */
    fun toUserResponseListAdmin(users: List<User?>): List<UserResponseAdmin> {
        return users.map { toUserResponseAdmin(it!!) }
    }
}
