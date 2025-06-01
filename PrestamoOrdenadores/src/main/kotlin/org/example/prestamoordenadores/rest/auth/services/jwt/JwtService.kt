package org.example.prestamoordenadores.rest.auth.services.jwt

import org.springframework.security.core.userdetails.UserDetails

/**
 * Interfaz para el servicio de gestión de JSON Web Tokens (JWT).
 *
 * Define las operaciones fundamentales para la creación, extracción y validación de tokens JWT.
 *
 * @author Natalia González Álvarez
 */
interface JwtService {
    /**
     * Extrae el nombre de usuario (sujeto) de un token JWT.
     *
     * @param token El token JWT del que se extraerá el nombre de usuario. Puede ser nulo.
     * @return El nombre de usuario contenido en el token, o `null` si el token es nulo o inválido.
     * @author Natalia González Álvarez
     */
    fun extractUserName(token: String?): String?

    /**
     * Genera un token JWT para un usuario dado.
     *
     * @param userDetails Los detalles del usuario para el que se generará el token. Puede ser nulo.
     * @return El token JWT generado como una cadena de texto, o `null` si los detalles del usuario son nulos.
     * @author Natalia González Álvarez
     */
    fun generateToken(userDetails: UserDetails?): String?

    /**
     * Valida un token JWT.
     *
     * Comprueba si el token es válido (no ha expirado, la firma es correcta, etc.) y si el nombre de usuario
     * en el token coincide con el de los detalles del usuario proporcionados.
     *
     * @param token El token JWT a validar. Puede ser nulo.
     * @param userDetails Los detalles del usuario con los que se comparará el token. Puede ser nulo.
     * @return `true` si el token es válido para el usuario, `false` en caso contrario.
     * @author Natalia González Álvarez
     */
    fun isTokenValid(token: String?, userDetails: UserDetails?): Boolean
}