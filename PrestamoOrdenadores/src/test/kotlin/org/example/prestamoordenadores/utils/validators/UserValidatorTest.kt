package org.example.prestamoordenadores.utils.validators

import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.users.dto.UserAvatarUpdateRequest
import org.example.prestamoordenadores.rest.users.dto.UserPasswordResetRequest
import org.example.prestamoordenadores.rest.users.dto.UserUpdateRequest
import org.example.prestamoordenadores.rest.users.models.Role
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserValidatorTest {

    private val currentYear = LocalDate.now().year

    @Test
    fun validateUserCreateRequest() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "Password123?",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validateUserCreateRequest Error when numeroIdentificacion en blanco`() {
        val request = UserCreateRequest(
            "",
            "name",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "Password123?",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El número de identificación no puede estar vacío", result.error.message)
    }

    @Test
    fun `validateUserCreateRequest Error when numeroIdentificacion invalido`() {
        val request = UserCreateRequest(
            "numIdentInválido",
            "name",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "Password123?",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El número de identificación no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when nombre en blanco`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "Password123?",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El nombre no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when apellidos en blanco`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "Password123?",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("Los apellidos no pueden estar vacíos", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when email en blanco`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "apellido",
            "",
            "curso",
            "tutor",
            "Password123?",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El email no es válido o está vacío", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when email invalido`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "apellido",
            "email@",
            "curso",
            "tutor",
            "Password123?",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El email no es válido o está vacío", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when password en blanco`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "",
            "Password123?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La contraseña no puede estar vacía", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when password invalido`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "pass123",
            "pass123"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La contraseña no es válida", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when confirmPassword en blanco`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "Password123?",
            ""
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La confirmación de contraseña no puede estar vacía", result.error.message)
    }

    @Test
    fun `validate UserCreateRequest Error when contrasenas diferentes`() {
        val request = UserCreateRequest(
            "${currentYear}LT123456",
            "name",
            "apellido",
            "email.loantech@gmail.com",
            "curso",
            "tutor",
            "Password123?",
            "Password234?"
        )

        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("Las contraseñas no coinciden", result.error.message)
    }

    @Test
    fun validateUserAvatarUpdateRequest() {
        val request = UserAvatarUpdateRequest(
            avatar = "imagen.png"
        )

        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate UserAvatarUpdateRequest Error when avatar en blanco`() {
        val request = UserAvatarUpdateRequest(
            avatar = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El avatar no puede estar vacío", result.error.message)
    }

    @Test
    fun validateUserLoginRequest() {
        val request = UserLoginRequest(
            email = "test@example.com",
            password = "Password123?"
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate UserLoginRequest Error when email en blanco`() {
        val request = UserLoginRequest(
            email = "",
            password = "Password123?"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El email no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate UserLoginRequest Error when password en blanco`() {
        val request = UserLoginRequest(
            email = "test@example.com",
            password = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La contraseña no puede estar vacía", result.error.message)
    }

    @Test
    fun validateUserPasswordResetRequest() {
        val request = UserPasswordResetRequest(
            oldPassword = "oldPass123!",
            newPassword = "newPass456#",
            confirmPassword = "newPass456#"
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate UserPasswordResetRequest Error when oldPassword en blanco`() {
        val request = UserPasswordResetRequest(
            oldPassword = "",
            newPassword = "newPass456#",
            confirmPassword = "newPass456#"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La contraseña antigua no puede estar vacía", result.error.message)
    }

    @Test
    fun `validate UserPasswordResetRequest Error when newPassword en blanco`() {
        val request = UserPasswordResetRequest(
            oldPassword = "oldPass123!",
            newPassword = "",
            confirmPassword = "newPass456#"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La contraseña nueva no puede estar vacía", result.error.message)
    }

    @Test
    fun `validate UserPasswordResetRequest Error when confirmPassword en blanco`() {
        val request = UserPasswordResetRequest(
            oldPassword = "oldPass123!",
            newPassword = "newPass456#",
            confirmPassword = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La confirmación de contraseña no puede estar vacía", result.error.message)
    }

    @Test
    fun `validate UserPasswordResetRequest Error when oldPassword y newPassword iguales`() {
        val request = UserPasswordResetRequest(
            oldPassword = "samePass!",
            newPassword = "samePass!",
            confirmPassword = "samePass!"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La contraseña nueva no puede ser igual a la antigua", result.error.message)
    }

    @Test
    fun `validate UserPasswordResetRequest Error when newPassword y confirmPassword diferentes`() {
        val request = UserPasswordResetRequest(
            oldPassword = "oldPass123!",
            newPassword = "newPass456#",
            confirmPassword = "differentPass@"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("Las contraseñas no coinciden", result.error.message)
    }

    @Test
    fun validateUserUpdateRequest() {
        val request = UserUpdateRequest(rol = Role.ADMIN.name, isActivo = true)
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate UserUpdateRequest Error when rol en blanco`() {
        val request = UserUpdateRequest(rol = "", isActivo = true)
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El rol no puede estar vacío o no es válido", result.error.message)
    }

    @Test
    fun `validate UserUpdateRequest Error when rol no válido`() {
        val request = UserUpdateRequest(rol = "NO_EXISTE", isActivo = true)
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El rol no puede estar vacío o no es válido", result.error.message)
    }
}