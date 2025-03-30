package org.example.prestamoordenadores.rest.users.services

import org.example.prestamoordenadores.rest.auth.exceptions.UserNotFoundException
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val repository: UserRepository,
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val userDetails = repository.findByEmail(username)
        if (userDetails == null) {
            throw UserNotFoundException(username)
        }
        return userDetails
    }
}