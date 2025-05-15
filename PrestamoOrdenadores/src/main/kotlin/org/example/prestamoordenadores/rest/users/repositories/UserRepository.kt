package org.example.prestamoordenadores.rest.users.repositories

import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByGuid(guid: String): User?
    fun findByCurso(curso: String): List<User?>
    fun findByNombre(nombre: String): User?
    fun findByEmail(email: String): User?
    fun findByTutor(tutor: String): List<User?>
    fun findUsersByRol(rol: Role): List<User?>
    fun existsUserByNombreAndApellidosAndCurso(nombre: String, apellidos:String, curso: String): Boolean
    fun findUserById(id: Long): User?
}