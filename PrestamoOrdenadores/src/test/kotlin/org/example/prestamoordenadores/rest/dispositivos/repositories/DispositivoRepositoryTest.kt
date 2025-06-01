package org.example.prestamoordenadores.rest.dispositivos.repositories

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@DataJpaTest
@Testcontainers
class DispositivoRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var dispositivoRepository: DispositivoRepository

    private val dispositivo1 = Dispositivo(
        "guidTest123",
        "5CD1234XYZ",
        "raton, cargador",
        EstadoDispositivo.DISPONIBLE,
        null,
        false,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val dispositivo2 = Dispositivo(
        "guidTest234",
        "6EF2345ABC",
        "raton, cargador",
        EstadoDispositivo.DISPONIBLE,
        null,
        false,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

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

    private var dispositivo3 = Dispositivo(
        "guidTest345",
        "7GH3456DEF",
        "raton, cargador",
        EstadoDispositivo.NO_DISPONIBLE,
        incidencia,
        true,
        LocalDateTime.now(),
        LocalDateTime.now()
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
        entityManager.persist(dispositivo1)
        entityManager.persist(dispositivo2)
        entityManager.persist(user)
        entityManager.persist(incidencia)
        entityManager.persist(dispositivo3)
        entityManager.flush()
    }

    @Test
    fun findByNumeroSerie() {
        val result = dispositivoRepository.findByNumeroSerie("5CD1234XYZ")

        assertEquals(dispositivo1.guid, result?.guid)
        assertEquals("5CD1234XYZ", result?.numeroSerie)
    }

    @Test
    fun findByEstadoDispositivo() {
        val disponibles = dispositivoRepository.findByEstadoDispositivo(EstadoDispositivo.DISPONIBLE)

        assertEquals(10, disponibles.size)
        assertEquals("ed472271676", disponibles[0].guid)
        assertEquals("1AB123WXYZ", disponibles[0].numeroSerie)
        assertEquals("b1a2z3y4x5w", disponibles[1].guid)
        assertEquals("1XY234CDEF", disponibles[1].numeroSerie)
    }

    @Test
    fun findDispositivoByGuid() {
        val found = dispositivoRepository.findDispositivoByGuid(dispositivo1.guid)
        val notFound = dispositivoRepository.findDispositivoByGuid("non-existent-guid")

        assertNotNull(found)
        assertEquals(dispositivo1.numeroSerie, found?.numeroSerie)
        assertNull(notFound)
    }

    @Test
    fun findDispositivoByIncidenciaGuid() {
        val found = dispositivoRepository.findDispositivoByIncidenciaGuid("guidTestINC")

        assertNotNull(found)
        assertEquals(dispositivo3.guid, found?.guid)
        assertEquals("7GH3456DEF", found?.numeroSerie)
    }

    @Test
    fun findByIncidenciaIdIn() {
        val incidenciaIds = listOf(incidencia.id)
        val foundDevices = dispositivoRepository.findByIncidenciaIdIn(incidenciaIds)

        assertNotNull(foundDevices)
        assertEquals(1, foundDevices.size)
        assertTrue(foundDevices.any { it.guid == dispositivo3.guid })
    }
}