package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.User
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
    override fun getAllUsers(): Result<List<User>, UserError> {
        logger.debug { "Getting all users" }
        return Ok(repository.findAll())
    }

    override fun getUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Getting user by GUID: $guid" }
        var user = repository.findByGuid(guid)

        return if (user == null) {
            Err(UserError.UserNotFound("User with GUID $guid not found"))
        } else {
            Ok(mapper.toUserResponse(user))
        }
    }

    override fun createUser(user: UserRequest): Result<UserResponse, UserError> {
        logger.debug { "Creating user: $user" }
        val existingUser = repository.findByUsername(user.username)
        if (existingUser!= null) {
            return Err(UserError.UserAlreadyExists("Username ${user.username} already exists"))
        }
        val savedUser = mapper.toUserFromCreate(user)
        repository.save(savedUser)
        return Ok(mapper.toUserResponse(savedUser))
    }

    override fun deleteUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Deleting user by GUID: $guid" }
        var user = repository.findByGuid(guid)
        if (user == null) {
            return Err(UserError.UserNotFound("User with GUID $guid not found"))
        }
        user.isActivo = false
        user.updatedDate = LocalDateTime.now()
        repository.save(user)
        return Ok(mapper.toUserResponse(user))
    }

    override fun getByUsername(username: String): Result<UserResponse?, UserError> {
        logger.debug { "Getting user by username: $username" }
        val user = repository.findByUsername(username)
        if (user == null) {
            return Err(UserError.UserNotFound("User with username $username not found"))
        }
        return Ok(mapper.toUserResponse(user))
    }

    override fun resetPassword(
        guid: String,
        user: UserPasswordResetRequest
    ): Result<UserResponse?, UserError> {
        logger.debug { "Resetting password for user with GUID: $guid" }
        val existingUser = repository.findByGuid(guid)
        if (existingUser == null) {
            return Err(UserError.UserNotFound("User with GUID $guid not found"))
        }
        existingUser.password = user.newPassword
        existingUser.updatedDate = LocalDateTime.now()
        existingUser.lastPasswordResetDate = LocalDateTime.now()
        val savedUser = repository.save(existingUser)
        return Ok(mapper.toUserResponse(savedUser))
    }
}