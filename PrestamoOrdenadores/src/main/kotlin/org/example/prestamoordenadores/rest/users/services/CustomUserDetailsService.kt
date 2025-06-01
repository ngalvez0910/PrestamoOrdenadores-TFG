package org.example.prestamoordenadores.rest.users.services

import org.example.prestamoordenadores.rest.auth.exceptions.UserNotFoundException
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

/**
 * Implementación personalizada de [UserDetailsService] para cargar datos específicos del usuario.
 *
 * Este servicio es responsable de recuperar los detalles del usuario del [UserRepository]
 * basándose en un nombre de usuario dado (en este caso, el correo electrónico del usuario).
 *
 * @property userRepository El repositorio utilizado para acceder a los datos del usuario.
 * @author Natalia González Álvarez
 */
@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * Carga los detalles del usuario por su nombre de usuario (correo electrónico).
     *
     * Este método forma parte de la interfaz [UserDetailsService] de Spring Security.
     * Intenta encontrar un usuario por su dirección de correo electrónico utilizando el [userRepository].
     * Si el usuario no se encuentra, se lanza una [UserNotFoundException].
     *
     * @param username El nombre de usuario (correo electrónico) del usuario a cargar.
     * @return Un objeto [UserDetails] que representa al usuario cargado.
     * @throws UserNotFoundException si el usuario con el nombre de usuario (correo electrónico) especificado no se encuentra.
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UserNotFoundException(username)

        return user
    }
}