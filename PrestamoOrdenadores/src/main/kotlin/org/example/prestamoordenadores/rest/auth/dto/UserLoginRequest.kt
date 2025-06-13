package org.example.prestamoordenadores.rest.auth.dto

import org.jetbrains.annotations.NotNull

/**
 * Clase de datos (Data Class) que representa la solicitud de inicio de sesión de un usuario.
 * Contiene las credenciales necesarias para autenticar a un usuario, con anotaciones de validación
 * para asegurar que los campos esenciales no sean nulos.
 *
 * @property email La dirección de correo electrónico del usuario. No puede ser nulo.
 * @property password La contraseña del usuario. No puede estar vacía.
 * @author Natalia González Álvarez
 */
data class UserLoginRequest (
    @NotNull("Email no puede ser null")
    val email: String,
    @NotNull("Password no puede estar vacío")
    val password: String
)