package org.example.prestamoordenadores.rest.sanciones.repositories

import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
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
import java.time.LocalDate
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SancionRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var sancionRepository: SancionRepository

    private val user1 = User(
        "userGuid1",
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

    private val sancion1 = Sancion(
        "guidTest123",
        user1,
        TipoSancion.ADVERTENCIA,
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    @BeforeEach
    fun setup() {
        entityManager.persist(user1)
        entityManager.persist(sancion1)
        entityManager.flush()
    }

    @Test
    fun findByGuid() {
        val foundSancion = sancionRepository.findByGuid("guidTest123")
        assertNotNull(foundSancion)
        assertEquals("guidTest123", foundSancion?.guid)
        assertEquals(TipoSancion.ADVERTENCIA, foundSancion?.tipoSancion)
    }

    @Test
    fun findByGuid_NotFound() {
        val notFoundSancion = sancionRepository.findByGuid("nonExistentGuid")
        assertNull(notFoundSancion)
    }

    @Test
    fun findSancionByFechaSancion() {
        val sancionesHoy = sancionRepository.findSancionByFechaSancion(LocalDate.now())
        assertEquals(1, sancionesHoy.size)
        assertTrue(sancionesHoy.any { it.guid == "guidTest123" })
    }

    @Test
    fun findSancionByFechaSancion_NotFound() {
        val sancionesManana = sancionRepository.findSancionByFechaSancion(LocalDate.of(2025, 4, 17))
        assertTrue(sancionesManana.isEmpty())
    }

    @Test
    fun findSancionByTipoSancion() {
        val bloqueos = sancionRepository.findSancionByTipoSancion(TipoSancion.ADVERTENCIA)
        assertEquals(1, bloqueos.size)
        assertTrue(bloqueos.any { it.guid == "guidTest123" })
    }

    @Test
    fun findSancionByTipoSancion_NotFound() {
        val suspensiones = sancionRepository.findSancionByTipoSancion(TipoSancion.BLOQUEO_TEMPORAL)
        assertTrue(suspensiones.isEmpty())
    }

    @Test
    fun findByUserGuid_shouldReturnListOfSancionesForMatchingUserGuid() {
        val user1Sanciones = sancionRepository.findByUserGuid("userGuid1")
        assertEquals(1, user1Sanciones.size)
        assertTrue(user1Sanciones.any { it.guid == "guidTest123" })
    }

    @Test
    fun findByUserGuid_shouldReturnEmptyListIfNoMatchingUserGuid() {
        val nonExistentUserSanciones = sancionRepository.findByUserGuid("nonExistentUserGuid")
        assertTrue(nonExistentUserSanciones.isEmpty())
    }
}