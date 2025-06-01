package org.example.prestamoordenadores.rest.auth.services.authentication

import org.example.prestamoordenadores.rest.auth.dto.JwtAuthResponse
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest

/**
 * Interfaz para el servicio de autenticación.
 *
 * Define las operaciones básicas para el registro ([signUp]) y el inicio de sesión ([signIn]) de usuarios,
 * devolviendo un [JwtAuthResponse] en caso de éxito.
 *
 * @author Natalia González Álvarez
 */
interface AuthenticationService {
    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request La solicitud que contiene los datos para crear el usuario. Puede ser nulo.
     * @return Un [JwtAuthResponse] con el token JWT si el registro es exitoso, o nulo en caso contrario.
     * @author Natalia González Álvarez
     */
    fun signUp(request: UserCreateRequest?): JwtAuthResponse?

    /**
     * Inicia sesión de un usuario existente.
     *
     * @param request La solicitud que contiene las credenciales del usuario para iniciar sesión. Puede ser nulo.
     * @return Un [JwtAuthResponse] con el token JWT si el inicio de sesión es exitoso, o nulo en caso contrario.
     * @author Natalia González Álvarez
     */
    fun signIn(request: UserLoginRequest?): JwtAuthResponse?
}