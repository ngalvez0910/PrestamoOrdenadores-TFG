package org.example.prestamoordenadores.rest.users.services

import org.example.prestamoordenadores.rest.auth.exceptions.UserNotFoundException
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        var user = userRepository.findByEmail(username)
        if (user == null) throw UserNotFoundException(username)

        return user
    }
}