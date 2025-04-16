package org.example.prestamoordenadores.rest.users.repositories

import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    private val user1 = User(
        "guid1",
        "email1@example.com",
        "password",
        Role.ALUMNO,
        "123",
        "John",
        "Doe",
        "1A",
        "Teacher A",
        "avatar1",
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val user2 = User(
        "guid2",
        "email2@example.com",
        "password",
        Role.PROFESOR,
        "456",
        "Jane",
        "Smith",
        "2B",
        "Teacher B",
        "avatar2",
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val user3 = User(
        "guid3",
        "email3@example.com",
        "password",
        Role.ALUMNO,
        "789",
        "John",
        "Smith",
        "1A",
        "Teacher A",
        "avatar3",
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    @BeforeEach
    fun setup() {
        entityManager.persist(user1)
        entityManager.persist(user2)
        entityManager.persist(user3)
        entityManager.flush()
    }

    @Test
    fun findByGuid() {
        val foundUser = userRepository.findByGuid("guid2")
        assertNotNull(foundUser)
        assertEquals("guid2", foundUser?.guid)
        assertEquals("email2@example.com", foundUser?.email)
    }

    @Test
    fun findByGuid_NotFound() {
        val notFoundUser = userRepository.findByGuid("nonExistentGuid")
        assertNull(notFoundUser)
    }

    @Test
    fun findByCurso() {
        val usersIn1A = userRepository.findByCurso("1A")
        assertEquals(2, usersIn1A.size)
        assertTrue(usersIn1A.any { it?.guid == "guid1" })
        assertTrue(usersIn1A.any { it?.guid == "guid3" })
    }

    @Test
    fun findByCurso_NotFound() {
        val usersIn3C = userRepository.findByCurso("3C")
        assertTrue(usersIn3C.isEmpty())
    }

    @Test
    fun findByNombre() {
        val foundUser = userRepository.findByNombre("Jane")
        assertNotNull(foundUser)
        assertEquals("guid2", foundUser?.guid)
        assertEquals("Smith", foundUser?.apellidos)
    }

    @Test
    fun findByNombre_NotFound() {
        val notFoundUser = userRepository.findByNombre("Peter")
        assertNull(notFoundUser)
    }

    @Test
    fun findByEmail() {
        val foundUser = userRepository.findByEmail("email3@example.com")
        assertNotNull(foundUser)
        assertEquals("guid3", foundUser?.guid)
        assertEquals("John", foundUser?.nombre)
    }

    @Test
    fun findByEmail_NotFound() {
        val notFoundUser = userRepository.findByEmail("nonexistent@example.com")
        assertNull(notFoundUser)
    }

    @Test
    fun findByTutor() {
        val usersByTeacherA = userRepository.findByTutor("Teacher A")
        assertEquals(2, usersByTeacherA.size)
        assertTrue(usersByTeacherA.any { it?.guid == "guid1" })
        assertTrue(usersByTeacherA.any { it?.guid == "guid3" })
    }

    @Test
    fun findByTutor_NotFound() {
        val usersByTeacherC = userRepository.findByTutor("Teacher C")
        assertTrue(usersByTeacherC.isEmpty())
    }

    @Test
    fun existsUserByNombreAndApellidosAndCurso() {
        val exists = userRepository.existsUserByNombreAndApellidosAndCurso("John", "Doe", "1A")
        assertTrue(exists)
    }

    @Test
    fun existsUserByNombreAndApellidosAndCurso_UserExists() {
        val exists = userRepository.existsUserByNombreAndApellidosAndCurso("John", "Smith", "1A")
        assertTrue(exists)
    }

    @Test
    fun existsUserByNombreAndApellidosAndCurso_NotFound() {
        val notExists = userRepository.existsUserByNombreAndApellidosAndCurso("Peter", "Pan", "3B")
        assertFalse(notExists)
    }

    @Test
    fun existsUserByNombreAndApellidosAndCurso_NombreNoCoincide() {
        val notExists = userRepository.existsUserByNombreAndApellidosAndCurso("Janet", "Doe", "1A")
        assertFalse(notExists)
    }

    @Test
    fun existsUserByNombreAndApellidosAndCurso_ApellidosNoCoincide() {
        val notExists = userRepository.existsUserByNombreAndApellidosAndCurso("John", "Wayne", "1A")
        assertFalse(notExists)
    }

    @Test
    fun existsUserByNombreAndApellidosAndCurso_CursoNoCoincide() {
        val notExists = userRepository.existsUserByNombreAndApellidosAndCurso("John", "Doe", "2B")
        assertFalse(notExists)
    }
}