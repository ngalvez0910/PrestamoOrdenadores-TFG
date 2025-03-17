package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["users"])
class UserServiceImpl(
    private val repository : UserRepository,
    private val mapper: UserMapper
): UserService {
    override suspend fun getAllUsers(): Result<List<UserResponseAdmin>, UserError> {
        logger.debug { "Obteniendo todos los usuarios" }

        return withContext(Dispatchers.IO) {
            val usuarios = repository.findAll()
            Ok(mapper.toUserResponseListAdmin(usuarios))
        }
    }

    @Cacheable(key = "#guid")
    override suspend fun getUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val user = repository.findByGuid(guid)
            if (user == null) {
                Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
            } else {
                Ok(mapper.toUserResponse(user))
            }
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun createUser(user: UserCreateRequest): Result<UserResponse, UserError> {
        logger.debug { "Creando usuario: $user" }

        return withContext(Dispatchers.IO) {
            if (repository.existsUserByNumeroIdentificacion(user.numeroIdentificacion)) {
                return@withContext Err(UserError.UserAlreadyExists("Usuario con numero de identificacion ${user.numeroIdentificacion} ya existe"))
            }

            val existingUser = repository.findByEmail(user.email)
            if (existingUser != null) {
                return@withContext Err(UserError.UserAlreadyExists("Usuario con email ${user.email} ya existe"))
            }

            if (repository.existsUserByNombreAndApellidosAndCurso(user.nombre, user.apellidos, user.curso)) {
                return@withContext Err(UserError.UserAlreadyExists("Usuario con nombre ${user.nombre}, apellidos ${user.apellidos} y curso ${user.curso} ya existe"))
            }

            val savedUser = repository.save(mapper.toUserFromCreate(user))
            Ok(mapper.toUserResponse(savedUser))
        }
    }

    @CachePut(key = "#result.guid")
    override suspend fun updateAvatar(guid: String, avatar: String): Result<UserResponse?, UserError> {
        logger.debug { "Actualizando avatar del usuario con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            var user = repository.findByGuid(guid)
            if (user == null) {
                return@withContext Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
            }
            user.avatar = avatar
            user.updatedDate = LocalDateTime.now()
            repository.save(user)
            Ok(mapper.toUserResponse(user))
        }
    }

    @CachePut(key = "#guid")
    override suspend fun deleteUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Eliminando usuario con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            var user = repository.findByGuid(guid)
            if (user == null) {
                return@withContext Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
            }
            user.isActivo = false
            user.updatedDate = LocalDateTime.now()
            repository.save(user)
            Ok(mapper.toUserResponse(user))
        }
    }

    @CachePut(key = "#guid")
    override suspend fun resetPassword(
        guid: String,
        user: UserPasswordResetRequest
    ): Result<UserResponse?, UserError> {
        logger.debug { "Cambiando contraseña de usuario con GUID: $guid" }

        return withContext(Dispatchers.IO) {
            val existingUser = repository.findByGuid(guid)
            if (existingUser == null) {
                return@withContext Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
            }
            existingUser.password = user.newPassword
            existingUser.updatedDate = LocalDateTime.now()
            existingUser.lastPasswordResetDate = LocalDateTime.now()
            val savedUser = repository.save(existingUser)
            Ok(mapper.toUserResponse(savedUser))
        }
    }

    @Cacheable(key = "#curso")
    override suspend fun getByCurso(curso: String): Result<List<UserResponse?>, UserError> {
        logger.debug { "Obteniendo usuarios del curso: $curso" }

        return withContext(Dispatchers.IO) {
            var user = repository.findByCurso(curso)
            if (user.isEmpty()) {
                return@withContext Err(UserError.UserNotFound("Usuario del curso $curso no encontrado"))
            }
            Ok(mapper.toUserResponseList(user))
        }
    }

    @Cacheable(key = "#nombre")
    override suspend fun getByNombre(nombre: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con el nombre: $nombre" }

        return withContext(Dispatchers.IO) {
            var user = repository.findByNombre(nombre)
            if (user == null) {
                return@withContext Err(UserError.UserNotFound("Usuario con nombre $nombre no encontrado"))
            }
            Ok(mapper.toUserResponse(user))
        }
    }

    @Cacheable(key = "#email")
    override suspend fun getByEmail(email: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con email: $email" }

        return withContext(Dispatchers.IO) {
            var user = repository.findByEmail(email)
            if (user == null) {
                return@withContext Err(UserError.UserNotFound("Usuario con email $email no encontrado"))
            }
            Ok(mapper.toUserResponse(user))
        }
    }

    @Cacheable(key = "#tutor")
    override suspend fun getByTutor(tutor: String): Result<List<UserResponse?>, UserError> {
        logger.debug { "Obteniendo usuarios con tutor: $tutor" }

        return withContext(Dispatchers.IO) {
            var users = repository.findByTutor(tutor)
            if (users.isEmpty()) {
                return@withContext Err(UserError.UserNotFound("Usuarios con tutor $tutor no encontrados"))
            }
            Ok(mapper.toUserResponseList(users))
        }
    }
}