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
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@DataJpaTest
@Testcontainers
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    private val user1 = User(
        "guidTest123",
        "email",
        "password",
        Role.ALUMNO,
        "numIdent",
        "name",
        "apellido",
        "curso",
        "tutor",
        "avatar",
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        false,
        false
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
        LocalDateTime.now(),
        false,
        false
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
        LocalDateTime.now(),
        false,
        false
    )

    companion object {
        @Container
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer("postgres:15.3")
            .withDatabaseName("prestamosDB-test")
            .withUsername("testuser")
            .withPassword("testpass")
    }

    @BeforeEach
    fun setup() {
        entityManager.clear()
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
        assertEquals(1, usersIn1A.size)
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
        assertEquals(1, usersByTeacherA.size)
        assertTrue(usersByTeacherA.any { it?.guid == "guid3" })
    }

    @Test
    fun findByTutor_NotFound() {
        val usersByTeacherC = userRepository.findByTutor("Teacher C")
        assertTrue(usersByTeacherC.isEmpty())
    }

    @Test
    fun existsUserByNombreAndApellidosAndCurso() {
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

    @Test
    fun findUsersByRol() {
        val estudiantes = userRepository.findUsersByRol(Role.ALUMNO)

        assertEquals(7, estudiantes.size)
        assertTrue(estudiantes.all { it?.rol == Role.ALUMNO })
    }
}