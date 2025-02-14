package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.stereotype.Service

@Service
interface UserService {
    fun getAllUsers(): Result<List<User>, UserError>
    fun getUserByGuid(guid: String) : Result<UserResponse?, UserError>
    fun createUser(user: UserRequest) : Result<UserResponse, UserError>
    fun deleteUserByGuid(guid: String) : Result<UserResponse?, UserError>
    fun getByUsername(username: String): Result<UserResponse?, UserError>
    fun resetPassword(guid: String, user: UserPasswordResetRequest) : Result<UserResponse?, UserError>
}