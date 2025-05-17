package org.example.prestamoordenadores.rest.incidencias.repositories

import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
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
class IncidenciaRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var incidenciaRepository: IncidenciaRepository

    private val user = User(
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

    private val incidencia = Incidencia(
        "guidTestINC",
        "Asunto",
        "Descripcion",
        EstadoIncidencia.PENDIENTE,
        user,
        LocalDateTime.now(),
        LocalDateTime.now(),
        false
    )

    @BeforeEach
    fun setup() {
        entityManager.persist(user)
        entityManager.persist(incidencia)
        entityManager.flush()
    }

    @Test
    fun findIncidenciaByGuid() {
        val found = incidenciaRepository.findIncidenciaByGuid("guidTestINC")

        assertNotNull(found)
        assertEquals("guidTestINC", found?.guid)
        assertEquals("Asunto", found?.asunto)
    }

    @Test
    fun findIncidenciaByGuid_NotFound() {
        val notFound = incidenciaRepository.findIncidenciaByGuid("nonExistentGuid")
        assertNull(notFound)
    }

    @Test
    fun findIncidenciasByEstadoIncidencia() {
        val pendientes = incidenciaRepository.findIncidenciasByEstadoIncidencia(EstadoIncidencia.PENDIENTE)

        assertEquals(1, pendientes.size)
        assertEquals("guidTestINC", pendientes[0].guid)
    }

    @Test
    fun findIncidenciasByEstadoIncidencia_NotFound() {
        val resueltas = incidenciaRepository.findIncidenciasByEstadoIncidencia(EstadoIncidencia.RESUELTO)
        assertTrue(resueltas.isEmpty())
    }

    @Test
    fun findIncidenciasByUserGuid() {
        val user1Incidencias = incidenciaRepository.findIncidenciasByUserGuid("guidTest123")

        assertEquals(1, user1Incidencias.size)
        assertTrue(user1Incidencias.any { it.guid == "guidTestINC" })
    }

    @Test
    fun findIncidenciasByUserGuid_NotFound() {
        val nonExistentUserIncidencias = incidenciaRepository.findIncidenciasByUserGuid("nonExistentUserGuid")
        assertTrue(nonExistentUserIncidencias.isEmpty())
    }

    @Test
    fun findIncidenciasByUserId() {
        val result = incidenciaRepository.findIncidenciasByUserId(user.id)

        assertEquals(1, result.size)
        assertTrue(result.all { it?.id == user.id })
    }

    @Test
    fun findIncidenciasByUserId_EmptyList() {
        val result = incidenciaRepository.findIncidenciasByUserId(999L)

        assertTrue(result.isEmpty())
    }
}