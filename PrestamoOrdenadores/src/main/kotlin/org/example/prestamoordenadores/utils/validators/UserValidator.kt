package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserRoleUpdateRequest
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.Role

fun UserCreateRequest.validate(): Result<UserCreateRequest, UserError> {
    if (this.numeroIdentificacion.isBlank()) {
        return Err(UserError.UserValidationError("El número de identificación no puede estar vacío"))
    } else if (this.nombre.isBlank())
        return Err(UserError.UserValidationError("El nombre no puede estar vacío"))
    else if (this.apellidos.isBlank())
        return Err(UserError.UserValidationError("Los apellidos no pueden estar vacíos"))
    else if (this.email.isBlank())
        return Err(UserError.UserValidationError("El email no puede estar vacío"))
    else if (this.fotoCarnet.isBlank())
        return Err(UserError.UserValidationError("La foto de carnet no puede estar vacía"))
    else if (this.password.isBlank())
        return Err(UserError.UserValidationError("La contraseña no puede estar vacía"))
    else if (this.confirmPassword.isBlank())
        return Err(UserError.UserValidationError("Confirmar contraseña no puede estar vacío"))

    return Ok(this)
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
    if (this.oldPassword.isBlank()) {
        return Err(UserError.UserValidationError("La contraseña antigua no puede estar vacía"))
    } else if (this.newPassword.isBlank())
        return Err(UserError.UserValidationError("La contraseña nueva no puede estar vacía"))
    else if (this.confirmPassword.isBlank())
        return Err(UserError.UserValidationError("Confirmar contraseña no puede estar vacío"))
    else if (this.newPassword == this.oldPassword)
        return Err(UserError.UserValidationError("La contraseña nueva no puede ser igual a la antigua"))
    else if (this.newPassword!= this.confirmPassword)
        return Err(UserError.UserValidationError("Las contraseñas no coinciden"))

    return Ok(this)
}