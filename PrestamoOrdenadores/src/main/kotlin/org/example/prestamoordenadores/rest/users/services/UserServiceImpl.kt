package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
class UserServiceImpl(
    private val repository : UserRepository,
    private val mapper: UserMapper
): UserService {
    override fun getAllUsers(): Result<List<UserResponseAdmin>, UserError> {
        logger.debug { "Obteniendo todos los usuarios" }
        var usuarios=repository.findAll()
        return Ok(mapper.toUserResponseListAdmin(usuarios))
    }

    override fun getUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con GUID: $guid" }
        var user = repository.findByGuid(guid)

        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        } else {
            Ok(mapper.toUserResponse(user))
        }
    }

    override fun createUser(user: UserCreateRequest): Result<UserResponse, UserError> {
        logger.debug { "Creando usuario: $user" }
        if(repository.existsUserByNumeroIdentificacion(user.numeroIdentificacion)){
            return Err(UserError.UserAlreadyExists("Usuario con numero de identificacion ${user.numeroIdentificacion} ya existe"))
        }

        val existingUser = repository.findByEmail(user.email)
        if (existingUser!= null) {
            return Err(UserError.UserAlreadyExists("Usuario con email ${user.email} ya existe"))
        }

        if (repository.existsUserByNombreAndApellidosAndCurso(user.nombre, user.apellidos, user.curso)){
            return Err(UserError.UserAlreadyExists("Usuario con nombre ${user.nombre}, apellidos ${user.apellidos} y curso ${user.curso} ya existe"))
        }

        val savedUser = mapper.toUserFromCreate(user)
        repository.save(savedUser)
        return Ok(mapper.toUserResponse(savedUser))
    }

    override fun updateAvatar(guid: String, avatar: String): Result<UserResponse?, UserError> {
        logger.debug { "Actualizando avatar del user con GUID: $guid" }
        var user = repository.findByGuid(guid)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }
        user.avatar = avatar
        user.updatedDate = LocalDateTime.now()
        repository.save(user)
        return Ok(mapper.toUserResponse(user))
    }

    override fun deleteUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Eliminando usuario con GUID: $guid" }
        var user = repository.findByGuid(guid)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }
        user.isActivo = false
        user.updatedDate = LocalDateTime.now()
        repository.save(user)
        return Ok(mapper.toUserResponse(user))
    }

    override fun resetPassword(
        guid: String,
        user: UserPasswordResetRequest
    ): Result<UserResponse?, UserError> {
        logger.debug { "Cambiando contraseña de usuario con GUID: $guid" }
        val existingUser = repository.findByGuid(guid)
        if (existingUser == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }
        existingUser.password = user.newPassword
        existingUser.updatedDate = LocalDateTime.now()
        existingUser.lastPasswordResetDate = LocalDateTime.now()
        val savedUser = repository.save(existingUser)
        return Ok(mapper.toUserResponse(savedUser))
    }

    override fun getByCurso(curso: String): Result<List<UserResponse?>, UserError> {
        logger.debug { "Obteniendo usuario del curso: $curso" }
        var user = repository.findByCurso(curso)

        return if (user.isEmpty()) {
            Err(UserError.UserNotFound("Usuario del curso $curso no encontrado"))
        } else {
            Ok(mapper.toUserResponseList(user))
        }
    }

    override fun getByNombre(nombre: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con el nombre: $nombre" }
        var user = repository.findByNombre(nombre)

        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con nombre $nombre no encontrado"))
        } else {
            Ok(mapper.toUserResponse(user))
        }
    }

    override fun getByEmail(email: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con email: $email" }
        var user = repository.findByEmail(email)

        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con email $email no encontrado"))
        } else {
            Ok(mapper.toUserResponse(user))
        }
    }
}