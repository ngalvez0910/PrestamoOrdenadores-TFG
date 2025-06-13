package org.example.prestamoordenadores.rest.auth.dto

/**
 * Clase de datos (Data Class) que representa la respuesta de autenticación JWT.
 * Contiene el token JWT generado tras un inicio de sesión o registro exitoso.
 *
 * @property token El token de autenticación JWT. Puede ser nulo.
 * @author Natalia González Álvarez
 */
data class JwtAuthResponse (
    val token: String? = null
)