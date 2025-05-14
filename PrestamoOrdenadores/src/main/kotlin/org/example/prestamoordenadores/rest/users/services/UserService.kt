package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.*
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service

@Service
interface UserService {
    fun getAllUsers(page: Int, size: Int): Result<PagedResponse<UserResponseAdmin>, UserError>
    fun getUserByGuid(guid: String) : Result<UserResponse?, UserError>
    fun updateAvatar(guid: String, user: UserAvatarUpdateRequest) : Result<UserResponse?, UserError>
    fun deleteUserByGuid(guid: String) : Result<UserResponse?, UserError>
    fun resetPassword(guid: String, user: UserPasswordResetRequest) : Result<UserResponse?, UserError>
    fun getByCurso(curso: String): Result<List<UserResponse?>, UserError>
    fun getByNombre(nombre: String): Result<UserResponse?, UserError>
    fun getByEmail(email: String): Result<UserResponse?, UserError>
    fun getByTutor(tutor: String) : Result<List<UserResponse?>, UserError>
    fun getUserByGuidAdmin(guid: String) : Result<UserResponseAdmin?, UserError>
    fun getUserById(id: Long): Result<User?, UserError>
    fun updateUser(guid: String, request: UserUpdateRequest): Result<UserResponseAdmin?, UserError>
}