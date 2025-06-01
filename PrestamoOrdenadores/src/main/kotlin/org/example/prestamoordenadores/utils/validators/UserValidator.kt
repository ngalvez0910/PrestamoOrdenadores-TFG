package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserUpdateRequest
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.models.Role
import java.time.LocalDate

/**
 * Función de extensión para validar un objeto [UserCreateRequest].
 *
 * Realiza las siguientes validaciones:
 * - [numeroIdentificacion] no puede estar en blanco y debe coincidir con el formato de carnet generado por [generarRegexCarnet].
 * - [nombre] no puede estar en blanco.
 * - [apellidos] no pueden estar en blanco.
 * - [email] no puede estar en blanco y debe ser un formato de correo electrónico válido.
 * - [password] no puede estar en blanco y debe cumplir con una política de seguridad (al menos 8 caracteres, una mayúscula, una minúscula, un dígito y un carácter especial).
 * - [confirmPassword] no puede estar en blanco.
 * - [password] y [confirmPassword] deben coincidir.
 *
 * @receiver La instancia de [UserCreateRequest] a validar.
 * @return Un [Result] que contiene el [UserCreateRequest] validado si la validación es exitosa ([Ok]),
 * o un [UserError.UserValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun UserCreateRequest.validate(): Result<UserCreateRequest, UserError> {
    val regex = generarRegexCarnet()
    return when {
        numeroIdentificacion!!.isBlank() || !numeroIdentificacion!!.matches(regex) ->
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

/**
 * Función de extensión para validar un objeto [UserAvatarUpdateRequest].
 *
 * Realiza la siguiente validación:
 * - El campo [avatar] no puede estar en blanco.
 *
 * @receiver La instancia de [UserAvatarUpdateRequest] a validar.
 * @return Un [Result] que contiene el [UserAvatarUpdateRequest] validado si la validación es exitosa ([Ok]),
 * o un [UserError.UserValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun UserAvatarUpdateRequest.validate(): Result<UserAvatarUpdateRequest, UserError> {
    if (this.avatar.isBlank()) {
        return Err(UserError.UserValidationError("El avatar no puede estar vacío"))
    }

    return Ok(this)
}

/**
 * Función de extensión para validar un objeto [UserUpdateRequest].
 *
 * Realiza la siguiente validación:
 * - El campo [rol] no puede estar en blanco y debe ser uno de los valores válidos definidos en [Role].
 *
 * @receiver La instancia de [UserUpdateRequest] a validar.
 * @return Un [Result] que contiene el [UserUpdateRequest] validado si la validación es exitosa ([Ok]),
 * o un [UserError.UserValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun UserUpdateRequest.validate(): Result<UserUpdateRequest, UserError> {
    if (this.rol?.isBlank() == true || this.rol?.uppercase() !in Role.entries.map { it.name }) {
        return Err(UserError.UserValidationError("El rol no puede estar vacío o no es válido"))
    }

    return Ok(this)
}

/**
 * Función de extensión para validar un objeto [UserLoginRequest].
 *
 * Realiza las siguientes validaciones:
 * - El campo [email] no puede estar en blanco.
 * - El campo [password] no puede estar en blanco.
 *
 * @receiver La instancia de [UserLoginRequest] a validar.
 * @return Un [Result] que contiene el [UserLoginRequest] validado si la validación es exitosa ([Ok]),
 * o un [UserError.UserValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun UserLoginRequest.validate(): Result<UserLoginRequest, UserError> {
    if (this.email.isBlank()) {
        return Err(UserError.UserValidationError("El email no puede estar vacío"))
    } else if (this.password.isBlank())
        return Err(UserError.UserValidationError("La contraseña no puede estar vacía"))

    return Ok(this)
}

/**
 * Función de extensión para validar un objeto [UserPasswordResetRequest].
 *
 * Realiza las siguientes validaciones:
 * - [oldPassword] no puede estar en blanco.
 * - [newPassword] no puede estar en blanco.
 * - [confirmPassword] no puede estar en blanco.
 * - [newPassword] no puede ser igual a [oldPassword].
 * - [newPassword] y [confirmPassword] deben coincidir.
 *
 * @receiver La instancia de [UserPasswordResetRequest] a validar.
 * @return Un [Result] que contiene el [UserPasswordResetRequest] validado si la validación es exitosa ([Ok]),
 * o un [UserError.UserValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
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

/**
 * Genera una expresión regular para validar el formato de un carnet de identificación.
 *
 * El formato esperado es un año (entre el año actual y 30 años atrás) seguido de "LT" y 6 dígitos.
 * Ejemplo: "2024LT123456".
 *
 * @return Una [Regex] que representa el patrón de carnet válido.
 * @author Natalia González Álvarez
 */
fun generarRegexCarnet(): Regex {
    val añoActual = LocalDate.now().year
    val añoInicio = añoActual - 30

    val añosValidos = (añoInicio..añoActual).joinToString("|")
    val regex = "($añosValidos)LT\\d{6}"

    return Regex(regex)
}