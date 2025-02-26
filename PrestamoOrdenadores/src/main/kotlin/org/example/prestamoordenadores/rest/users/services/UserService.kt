package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.springframework.stereotype.Service

@Service
interface UserService {
    fun getAllUsers(): Result<List<UserResponse>, UserError>
    fun getUserByGuid(guid: String) : Result<UserResponse?, UserError>
    fun createUser(user: UserCreateRequest) : Result<UserResponse, UserError>
    fun updateAvatar(guid: String, avatar: String) : Result<UserResponse?, UserError>
    fun deleteUserByGuid(guid: String) : Result<UserResponse?, UserError>
    fun resetPassword(guid: String, user: UserPasswordResetRequest) : Result<UserResponse?, UserError>
    fun getByCurso(curso: String): Result<List<UserResponse?>, UserError>
    fun getByNombre(nombre: String): Result<UserResponse?, UserError>
    fun getByEmail(email: String): Result<UserResponse?, UserError>
}