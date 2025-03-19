package org.example.prestamoordenadores.rest.dispositivos.repositories

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import kotlin.test.assertEquals

/*
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DispositivoRepositoryTest{

    @Autowired
    lateinit var dispositivoRepository: DispositivoRepository

    @Autowired
    lateinit var entityManager: TestEntityManager

    private val dispositivo = Dispositivo(guid = "guidTest123", numeroSerie = "5CD1234XYZ", stock = 20)
    private val dispositivo2 = Dispositivo(guid = "guidTest234", numeroSerie = "CNB4567ABC", stock = 10)

    @BeforeEach
    fun setUp() {
        entityManager.merge<Any?>(dispositivo)
        entityManager.merge<Any?>(dispositivo2)
        entityManager.flush()
    }

    @Test
    fun findByNumeroSerieTest() {
        val res = dispositivoRepository.findByNumeroSerie(dispositivo.numeroSerie)

        assertAll(
            { assertEquals(res, dispositivo) },
            { assert(res.numeroSerie == dispositivo.numeroSerie) }
        )
    }

    @Test
    fun findByEstadoDispositivoTest() {
        val res = dispositivoRepository.findByEstadoDispositivo(dispositivo.estadoDispositivo)

        assertAll(
            { assertEquals(res, listOf(dispositivo)) },
            { assert(res[0].estadoDispositivo == dispositivo.estadoDispositivo) }
        )
    }

    @Test
    fun findDispositivoByGuidTest() {
        val res = dispositivoRepository.findDispositivoByGuid(dispositivo.guid)

        assertAll(
            { assertEquals(res, dispositivo) },
            { assert(res?.guid == dispositivo.guid) }
        )
    }
}*/