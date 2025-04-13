package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserRoleUpdateRequest
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.Role

fun UserCreateRequest.validate(): Result<UserCreateRequest, UserError> {
    return when {
        numeroIdentificacion!!.isBlank() ->
            Err(UserError.UserValidationError("El número de identificación no puede estar vacío"))

        nombre!!.isBlank() ->
            Err(UserError.UserValidationError("El nombre no puede estar vacío"))

        apellidos!!.isBlank() ->
            Err(UserError.UserValidationError("Los apellidos no pueden estar vacíos"))

        email!!.isBlank() || !email!!.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) ->
            Err(UserError.UserValidationError("El email no es válido o está vacío"))

        password!!.isBlank() ->
            Err(UserError.UserValidationError("La contraseña no puede estar vacía"))

        !password!!.matches(Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,}$")) ->
            Err(UserError.UserValidationError("La contraseña no es válida"))

        confirmPassword!!.isBlank() ->
            Err(UserError.UserValidationError("La confirmación de contraseña no puede estar vacía"))

        password != confirmPassword ->
            Err(UserError.UserValidationError("Las contraseñas no coinciden"))

        else -> Ok(this)
    }
}

fun UserAvatarUpdateRequest.validate(): Result<UserAvatarUpdateRequest, UserError> {
    if (this.avatar.isBlank()) {
        return Err(UserError.UserValidationError("El avatar no puede estar vacío"))
    }

    return Ok(this)
}

fun UserRoleUpdateRequest.validate(): Result<UserRoleUpdateRequest, UserError> {
    if (this.rol.isBlank() || this.rol.uppercase() !in Role.entries.map { it.name }) {
        return Err(UserError.UserValidationError("El rol no puede estar vacío o no es válido"))
    }

    return Ok(this)
}

fun UserLoginRequest.validate(): Result<UserLoginRequest, UserError> {
    if (this.email.isBlank()) {
        return Err(UserError.UserValidationError("El email no puede estar vacío"))
    } else if (this.password.isBlank())
        return Err(UserError.UserValidationError("La contraseña no puede estar vacía"))

    return Ok(this)
}

fun UserPasswordResetRequest.validate(): Result<UserPasswordResetRequest, UserError> {
    return when {
        oldPassword.isBlank() ->
            Err(UserError.UserValidationError("La contraseña antigua no puede estar vacía"))

        newPassword.isBlank() ->
            Err(UserError.UserValidationError("La contraseña nueva no puede estar vacía"))

        confirmPassword.isBlank() ->
            Err(UserError.UserValidationError("La confirmación de contraseña no puede estar vacía"))

        newPassword == oldPassword ->
            Err(UserError.UserValidationError("La contraseña nueva no puede ser igual a la antigua"))

        newPassword!= confirmPassword ->
            Err(UserError.UserValidationError("Las contraseñas no coinciden"))

        else -> Ok(this)
    }
}