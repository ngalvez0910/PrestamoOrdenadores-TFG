package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.*
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.springframework.stereotype.Service

/**
 * Interfaz de servicio para la gestión de operaciones relacionadas con [User].
 *
 * Define las operaciones disponibles para interactuar con los usuarios, incluyendo
 * la obtención, actualización, eliminación y restablecimiento de contraseñas, entre otras.
 * Todas las operaciones devuelven un [Result] que encapsula el éxito ([Ok]) o el error ([Err])
 * de la operación, siendo el error de tipo [UserError].
 *
 * @author Natalia González Álvarez
 */
@Service
interface UserService {

    /**
     * Obtiene una lista paginada de todos los usuarios, con detalles de administrador.
     *
     * @param page El número de página (basado en 0).
     * @param size El tamaño de la página.
     * @return Un [Result] que contiene un [PagedResponse] de [UserResponseAdmin] si tiene éxito, o un [UserError] si falla.
     */
    fun getAllUsers(page: Int, size: Int): Result<PagedResponse<UserResponseAdmin>, UserError>

    /**
     * Obtiene un usuario por su GUID (identificador único global).
     *
     * @param guid El GUID del usuario a buscar.
     * @return Un [Result] que contiene un [UserResponse] si el usuario se encuentra, o `null` si no, o un [UserError] si falla.
     */
    fun getUserByGuid(guid: String) : Result<UserResponse?, UserError>

    /**
     * Actualiza el avatar de un usuario.
     *
     * @param guid El GUID del usuario cuyo avatar se va a actualizar.
     * @param user El DTO [UserAvatarUpdateRequest] con la información del nuevo avatar.
     * @return Un [Result] que contiene un [UserResponse] actualizado si tiene éxito, o `null` si no, o un [UserError] si falla.
     */
    fun updateAvatar(guid: String, user: UserAvatarUpdateRequest) : Result<UserResponse?, UserError>

    /**
     * Elimina un usuario por su GUID.
     *
     * @param guid El GUID del usuario a eliminar.
     * @return Un [Result] que contiene un [UserResponseAdmin] del usuario eliminado si tiene éxito, o `null` si no, o un [UserError] si falla.
     */
    fun deleteUserByGuid(guid: String) : Result<UserResponseAdmin?, UserError>

    /**
     * Restablece la contraseña de un usuario.
     *
     * @param guid El GUID del usuario cuya contraseña se va a restablecer.
     * @param user El DTO [UserPasswordResetRequest] con la nueva contraseña.
     * @return Un [Result] que contiene un [UserResponse] si tiene éxito, o `null` si no, o un [UserError] si falla.
     */
    fun resetPassword(guid: String, user: UserPasswordResetRequest) : Result<UserResponse?, UserError>

    /**
     * Obtiene una lista de usuarios por su curso.
     *
     * @param curso El curso por el que filtrar los usuarios.
     * @return Un [Result] que contiene una lista de [UserResponse] si tiene éxito, o un [UserError] si falla.
     */
    fun getByCurso(curso: String): Result<List<UserResponse?>, UserError>

    /**
     * Obtiene un usuario por su nombre.
     *
     * @param nombre El nombre del usuario a buscar.
     * @return Un [Result] que contiene un [UserResponse] si el usuario se encuentra, o `null` si no, o un [UserError] si falla.
     */
    fun getByNombre(nombre: String): Result<UserResponse?, UserError>

    /**
     * Obtiene un usuario por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico del usuario a buscar.
     * @return Un [Result] que contiene un [UserResponse] si el usuario se encuentra, o `null` si no, o un [UserError] si falla.
     */
    fun getByEmail(email: String): Result<UserResponse?, UserError>

    /**
     * Obtiene una lista de usuarios por su tutor.
     *
     * @param tutor El tutor por el que filtrar los usuarios.
     * @return Un [Result] que contiene una lista de [UserResponse] si tiene éxito, o un [UserError] si falla.
     */
    fun getByTutor(tutor: String) : Result<List<UserResponse?>, UserError>

    /**
     * Obtiene un usuario por su GUID con detalles de administrador.
     *
     * @param guid El GUID del usuario a buscar.
     * @return Un [Result] que contiene un [UserResponseAdmin] si el usuario se encuentra, o `null` si no, o un [UserError] si falla.
     */
    fun getUserByGuidAdmin(guid: String) : Result<UserResponseAdmin?, UserError>

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id El ID del usuario a buscar.
     * @return Un [Result] que contiene un [User] si el usuario se encuentra, o `null` si no, o un [UserError] si falla.
     */
    fun getUserById(id: Long): Result<User?, UserError>

    /**
     * Actualiza la información de un usuario.
     *
     * @param guid El GUID del usuario a actualizar.
     * @param request El DTO [UserUpdateRequest] con la información del usuario a actualizar.
     * @return Un [Result] que contiene un [UserResponseAdmin] actualizado si tiene éxito, o `null` si no, o un [UserError] si falla.
     */
    fun updateUser(guid: String, request: UserUpdateRequest): Result<UserResponseAdmin?, UserError>

    /**
     * Implementa el "derecho al olvido" para un usuario, eliminando sus datos sensibles.
     *
     * @param userGuid El GUID del usuario sobre el que se aplicará el derecho al olvido.
     * @return Un [Result] que contiene [Unit] si la operación tiene éxito, o un [UserError] si falla.
     */
    fun derechoAlOlvido(userGuid: String): Result<Unit, UserError>
}