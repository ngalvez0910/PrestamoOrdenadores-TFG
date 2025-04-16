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

    private val user1 = User(
        "userGuid1",
        "email1",
        "password",
        Role.ALUMNO,
        "numIdent1",
        "name1",
        "apellido1",
        "curso1",
        "tutor1",
        "avatar1",
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val incidencia1 = Incidencia(
        "guidTest123",
        "Asunto",
        "Descripcion",
        EstadoIncidencia.PENDIENTE,
        user1,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    @BeforeEach
    fun setup() {
        entityManager.persist(user1)
        entityManager.persist(incidencia1)
        entityManager.flush()
    }

    @Test
    fun findIncidenciaByGuid() {
        val found = incidenciaRepository.findIncidenciaByGuid("guidTest123")

        assertNotNull(found)
        assertEquals("guidTest123", found?.guid)
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
        assertEquals("guidTest123", pendientes[0].guid)
    }

    @Test
    fun findIncidenciasByEstadoIncidencia_NotFound() {
        val resueltas = incidenciaRepository.findIncidenciasByEstadoIncidencia(EstadoIncidencia.RESUELTO)
        assertTrue(resueltas.isEmpty())
    }

    @Test
    fun findIncidenciasByUserGuid() {
        val user1Incidencias = incidenciaRepository.findIncidenciasByUserGuid("userGuid1")

        assertEquals(1, user1Incidencias.size)
        assertTrue(user1Incidencias.any { it.guid == "guidTest123" })
    }

    @Test
    fun findIncidenciasByUserGuid_NotFound() {
        val nonExistentUserIncidencias = incidenciaRepository.findIncidenciasByUserGuid("nonExistentUserGuid")
        assertTrue(nonExistentUserIncidencias.isEmpty())
    }
}