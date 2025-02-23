package org.example.prestamoordenadores.rest.users.repositories

import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.users.dto.UserResponse
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): UserResponse?
    fun findByGuid(guid: String): UserResponse?
    fun save(userRequest: UserCreateRequest): UserResponse?
    fun findByCurso(curso: String): List<UserResponse?>
    fun findByNombre(nombre: String): UserResponse?
    fun findByEmail(email: String): UserResponse?
    fun findByTutor(tutor: String): UserResponse?
    fun existsAlumnoByNombreAndApellidosAndCurso(nombre: String, apellidos:String, curso: String): Boolean
}