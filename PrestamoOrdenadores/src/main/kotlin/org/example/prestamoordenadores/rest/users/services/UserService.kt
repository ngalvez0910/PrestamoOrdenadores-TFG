package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.*
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
interface UserService {
    fun getAllUsers(page: Int, size: Int): Result<List<UserResponseAdmin>, UserError>
    suspend fun getUserByGuid(guid: String) : Result<UserResponse?, UserError>
    suspend fun createUser(user: UserCreateRequest) : Result<UserResponse, UserError>
    suspend fun updateAvatar(guid: String, user: UserAvatarUpdateRequest) : Result<UserResponse?, UserError>
    suspend fun deleteUserByGuid(guid: String) : Result<UserResponse?, UserError>
    suspend fun resetPassword(guid: String, user: UserPasswordResetRequest) : Result<UserResponse?, UserError>
    suspend fun getByCurso(curso: String): Result<List<UserResponse?>, UserError>
    suspend fun getByNombre(nombre: String): Result<UserResponse?, UserError>
    suspend fun getByEmail(email: String): Result<UserResponse?, UserError>
    suspend fun getByTutor(tutor: String) : Result<List<UserResponse?>, UserError>
    suspend fun getUserByGuidAdmin(guid: String) : Result<UserResponseAdmin?, UserError>
    suspend fun updateRole(guid: String, user: UserRoleUpdateRequest) : Result<UserResponseAdmin?, UserError>
    suspend fun getUserById(id: Long): Result<User?, UserError>
}