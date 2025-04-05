package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.*
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["users"])
class UserServiceImpl(
    private val repository : UserRepository,
    private val mapper: UserMapper,
): UserService {
    override fun getAllUsers(page: Int, size: Int): Result<List<UserResponseAdmin>, UserError> {
        logger.debug { "Obteniendo todos los usuarios" }

        val pageRequest = PageRequest.of(page, size)
        val pageUsers = repository.findAll(pageRequest)
        val usersResponses = mapper.toUserResponseListAdmin(pageUsers.content)

        return Ok(usersResponses)
    }

    @Cacheable(key = "#guid")
    override fun getUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con GUID: $guid" }

        val user = repository.findByGuid(guid)
        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        } else {
            Ok(mapper.toUserResponse(user))
        }
    }

    @CachePut(key = "#result.guid")
    override fun updateAvatar(guid: String, user: UserAvatarUpdateRequest): Result<UserResponse?, UserError> {
        logger.debug { "Actualizando avatar del usuario con GUID: $guid" }

        val userValidado = user.validate()
        if (userValidado.isErr) {
            return Err(UserError.UserValidationError("Usuario inválido"))
        }

        var userEncontrado = repository.findByGuid(guid)
        if (userEncontrado == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }

        userEncontrado.avatar = user.avatar
        userEncontrado.updatedDate = LocalDateTime.now()
        repository.save(userEncontrado)
        return Ok(mapper.toUserResponse(userEncontrado))
    }

    @CachePut(key = "#guid")
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

    @CachePut(key = "#guid")
    override fun resetPassword(
        guid: String,
        user: UserPasswordResetRequest
    ): Result<UserResponse?, UserError> {
        logger.debug { "Cambiando contraseña de usuario con GUID: $guid" }

        val userValidado = user.validate()
        if (userValidado.isErr) {
            return Err(UserError.UserValidationError("Usuario inválido"))
        }

        val existingUser = repository.findByGuid(guid)
        if (existingUser == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }

        existingUser.campoPassword = user.newPassword
        existingUser.updatedDate = LocalDateTime.now()
        existingUser.lastPasswordResetDate = LocalDateTime.now()

        val savedUser = repository.save(existingUser)
        return Ok(mapper.toUserResponse(savedUser))
    }

    @Cacheable(key = "#curso")
    override fun getByCurso(curso: String): Result<List<UserResponse?>, UserError> {
        logger.debug { "Obteniendo usuarios del curso: $curso" }

        var user = repository.findByCurso(curso)
        if (user.isEmpty()) {
            return Err(UserError.UserNotFound("Usuario del curso $curso no encontrado"))
        }

        return Ok(mapper.toUserResponseList(user))
    }

    @Cacheable(key = "#nombre")
    override fun getByNombre(nombre: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con el nombre: $nombre" }

        var user = repository.findByNombre(nombre)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con nombre $nombre no encontrado"))
        }

        return Ok(mapper.toUserResponse(user))
    }

    @Cacheable(key = "#email")
    override fun getByEmail(email: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con email: $email" }

        var user = repository.findByEmail(email)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con email $email no encontrado"))
        }

        return Ok(mapper.toUserResponse(user))
    }

    @Cacheable(key = "#tutor")
    override fun getByTutor(tutor: String): Result<List<UserResponse?>, UserError> {
        logger.debug { "Obteniendo usuarios con tutor: $tutor" }

        var users = repository.findByTutor(tutor)
        if (users.isEmpty()) {
            return Err(UserError.UserNotFound("Usuarios con tutor $tutor no encontrados"))
        }

        return Ok(mapper.toUserResponseList(users))
    }

    @Cacheable(key = "#guid")
    override fun getUserByGuidAdmin(guid: String): Result<UserResponseAdmin?, UserError> {
        logger.debug { "Obteniendo usuario con GUID: $guid" }

        val user = repository.findByGuid(guid)
        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        } else {
            Ok(mapper.toUserResponseAdmin(user))
        }
    }

    @CachePut(key = "#result.guid")
    override fun updateRole(guid: String, user: UserRoleUpdateRequest): Result<UserResponseAdmin?, UserError> {
        logger.debug { "Actualizando rol del usuario con GUID: $guid" }

        val userValidado = user.validate()
        if (userValidado.isErr) {
            return Err(UserError.UserValidationError("Usuario inválido"))
        }

        var userEncontrado = repository.findByGuid(guid)
        if (userEncontrado == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }

        val rolNormalizado = user.rol.replace(" ", "_").uppercase()
        userEncontrado.rol = Role.valueOf(rolNormalizado)

        if (userEncontrado.rol == Role.PROFESOR){
            userEncontrado.tutor = ""
        } else if (userEncontrado.rol == Role.ADMIN){
            userEncontrado.curso = ""
            userEncontrado.tutor = ""
        }

        userEncontrado.updatedDate = LocalDateTime.now()

        repository.save(userEncontrado)

        return Ok(mapper.toUserResponseAdmin(userEncontrado))
    }

    @Cacheable(key = "#id")
    override fun getUserById(id: Long): Result<User?, UserError> {
        logger.debug { "Obteniendo usuario con ID: $id" }

        val user = repository.findById(id).orElse(null)
        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con ID $id no encontrado"))
        } else {
            Ok(user)
        }
    }
}