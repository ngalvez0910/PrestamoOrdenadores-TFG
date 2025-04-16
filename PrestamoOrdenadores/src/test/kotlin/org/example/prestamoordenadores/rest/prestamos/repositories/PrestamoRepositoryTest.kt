package org.example.prestamoordenadores.rest.prestamos.repositories

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
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
class PrestamoRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var prestamoRepository: PrestamoRepository

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

    private val dispositivo1 = Dispositivo(
        "deviceGuid1",
        "serial1",
        "desc1",
        EstadoDispositivo.DISPONIBLE,
        null,
        true,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    private val prestamo1 = Prestamo(
        "guidTest123",
        user1,
        dispositivo1,
        EstadoPrestamo.EN_CURSO,
        LocalDate.now(),
        LocalDate.now(),
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    @BeforeEach
    fun setup() {
        entityManager.persist(user1)
        entityManager.persist(dispositivo1)
        entityManager.persist(prestamo1)
        entityManager.flush()
    }

    @Test
    fun findByGuid() {
        val foundPrestamo = prestamoRepository.findByGuid("guidTest123")
        assertNotNull(foundPrestamo)
        assertEquals("guidTest123", foundPrestamo?.guid)
        assertEquals(LocalDate.now(), foundPrestamo?.fechaPrestamo)
    }

    @Test
    fun findByGuid_NotFound() {
        val notFoundPrestamo = prestamoRepository.findByGuid("nonExistentGuid")
        assertNull(notFoundPrestamo)
    }

    @Test
    fun save() {
        val newDispositivo = Dispositivo(
            "guidTest098",
            "5CD1234XYZ",
            "raton, cargador",
            EstadoDispositivo.DISPONIBLE,
            null,
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        )
        entityManager.persist(newDispositivo)

        val newUser = User(
            "newUserGuid",
            "email2@example.com",
            "password",
            Role.ALUMNO,
            "123",
            "John",
            "Smith",
            "1A",
            "Teacher A",
            "avatar1",
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        val newPrestamo = Prestamo(
            "guidTest098",
            newUser,
            newDispositivo,
            EstadoPrestamo.EN_CURSO,
            LocalDate.now(),
            LocalDate.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )
        val savedPrestamo = prestamoRepository.save(newPrestamo)

        assertNotNull(savedPrestamo?.id)
        assertEquals("guidTest098", savedPrestamo?.guid)
        assertEquals(newUser.guid, savedPrestamo?.user?.guid)
        assertEquals(newDispositivo.guid, savedPrestamo?.dispositivo?.guid)
    }

    @Test
    fun findByFechaPrestamo() {
        val prestamosHoy = prestamoRepository.findByFechaPrestamo(LocalDate.now())
        assertEquals(1, prestamosHoy.size)
        assertTrue(prestamosHoy.any { it.guid == "guidTest123" })
    }

    @Test
    fun findByFechaPrestamo_NotFound() {
        val prestamosAyer = prestamoRepository.findByFechaPrestamo(LocalDate.of(2025, 2, 15).minusDays(1))
        assertTrue(prestamosAyer.isEmpty())
    }

    @Test
    fun findByFechaDevolucion() {
        val devoluciones18 = prestamoRepository.findByFechaDevolucion(LocalDate.now())
        assertEquals(1, devoluciones18.size)
        assertEquals("guidTest123", devoluciones18[0].guid)
    }

    @Test
    fun findByFechaDevolucion_NotFound() {
        val devolucionesManana = prestamoRepository.findByFechaDevolucion(LocalDate.now().plusDays(1))
        assertTrue(devolucionesManana.isEmpty())
    }

    @Test
    fun findByUserGuid() {
        val user1Prestamos = prestamoRepository.findByUserGuid("userGuid1")
        assertEquals(1, user1Prestamos.size)
        assertTrue(user1Prestamos.any { it.guid == "guidTest123" })
    }

    @Test
    fun findByUserGuid_NotFound() {
        val nonExistentUserPrestamos = prestamoRepository.findByUserGuid("nonExistentUserGuid")
        assertTrue(nonExistentUserPrestamos.isEmpty())
    }
}