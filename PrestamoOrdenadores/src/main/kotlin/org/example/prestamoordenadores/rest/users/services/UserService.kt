package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponseAdmin
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.springframework.stereotype.Service

@Service
interface UserService {
    suspend fun getAllUsers(): Result<List<UserResponseAdmin>, UserError>
    suspend fun getUserByGuid(guid: String) : Result<UserResponse?, UserError>
    suspend fun createUser(user: UserCreateRequest) : Result<UserResponse, UserError>
    suspend fun updateAvatar(guid: String, avatar: String) : Result<UserResponse?, UserError>
    suspend fun deleteUserByGuid(guid: String) : Result<UserResponse?, UserError>
    suspend fun resetPassword(guid: String, user: UserPasswordResetRequest) : Result<UserResponse?, UserError>
    suspend fun getByCurso(curso: String): Result<List<UserResponse?>, UserError>
    suspend fun getByNombre(nombre: String): Result<UserResponse?, UserError>
    suspend fun getByEmail(email: String): Result<UserResponse?, UserError>
    suspend fun getByTutor(tutor: String) : Result<List<UserResponse?>, UserError>
}