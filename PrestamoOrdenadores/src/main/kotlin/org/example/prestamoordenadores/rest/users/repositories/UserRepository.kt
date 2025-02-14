package org.example.prestamoordenadores.rest.users.repositories

import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}