package org.example.prestamoordenadores.rest.sanciones.repositories

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
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

    private val user = User(
        1,
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
        LocalDateTime.now()
    )

    private val dispositivo = Dispositivo(
        1,
        "guidTest123",
        "5CD1234XYZ",
        "raton, cargador",
        EstadoDispositivo.DISPONIBLE,
        null,
        LocalDateTime.now(),
        LocalDateTime.now(),
        false
    )

    private val prestamo = Prestamo(
        1,
        "guidTest123",
        user,
        dispositivo,
        EstadoPrestamo.EN_CURSO,
        LocalDate.now(),
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val sancion = Sancion(
        1L,
        "guidTest123",
        user,
        prestamo,
        TipoSancion.ADVERTENCIA,
        LocalDate.now(),
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        false
    )

    @BeforeEach
    fun setup() {
        entityManager.persist(user)
        entityManager.persist(sancion)
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

    @Test
    fun findByUserAndTipoSancion() {
        val sanciones = sancionRepository.findByUserAndTipoSancion(user, TipoSancion.ADVERTENCIA)
        assertEquals(1, sanciones.size)
        assertTrue(sanciones.any { it.guid == "guidTest123" })
    }

    @Test
    fun findByUserAndTipoSancion_shouldReturnEmptyListIfNoMatch() {
        val sanciones = sancionRepository.findByUserAndTipoSancion(user, TipoSancion.BLOQUEO_TEMPORAL)
        assertTrue(sanciones.isEmpty())
    }

    @Test
    fun existsByPrestamoGuidAndTipoSancion() {
        val exists = sancionRepository.existsByPrestamoGuidAndTipoSancion("guidTest123", TipoSancion.ADVERTENCIA)
        assertTrue(exists)
    }

    @Test
    fun existsByPrestamoGuidAndTipoSancion_shouldReturnFalseIfNotExists() {
        val exists = sancionRepository.existsByPrestamoGuidAndTipoSancion("nonexistentGuid", TipoSancion.BLOQUEO_TEMPORAL)
        assertFalse(exists)
    }

    @Test
    fun findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse() {
        val inactiveUser = User(id = 2, guid = "inactiveUser", isActivo = false)
        val inactiveSancion = Sancion(id = 2, guid = "inactiveSancion", user = inactiveUser, fechaFin = LocalDate.now())

        entityManager.persist(inactiveUser)
        entityManager.persist(inactiveSancion)
        entityManager.flush()

        val result = sancionRepository.findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(TipoSancion.ADVERTENCIA, LocalDate.now())
        assertTrue(result.any { it.guid == "inactiveSancion" })
    }


    @Test
    fun findSancionsByUserAndTipoSancionIn() {
        val tipos = listOf(TipoSancion.ADVERTENCIA, TipoSancion.BLOQUEO_TEMPORAL)
        val result = sancionRepository.findSancionsByUserAndTipoSancionIn(user, tipos)
        assertEquals(1, result.size)
        assertEquals("guidTest123", result.first().guid)
    }

    @Test
    fun findSancionsByUserAndTipoSancionIn_shouldReturnEmptyListForNonMatchingTipos() {
        val tipos = listOf(TipoSancion.BLOQUEO_TEMPORAL, TipoSancion.INDEFINIDO)
        val result = sancionRepository.findSancionsByUserAndTipoSancionIn(user, tipos)
        assertTrue(result.isEmpty())
    }

    @Test
    fun findSancionsByUserId() {
        val result = sancionRepository.findSancionsByUserId(user.id!!)
        assertEquals(1, result.size)
        assertEquals("guidTest123", result.first()?.guid)
    }

    @Test
    fun findSancionsByUserId_shouldReturnEmptyListIfUserIdNotFound() {
        val result = sancionRepository.findSancionsByUserId(999L)
        assertTrue(result.isEmpty())
    }
}