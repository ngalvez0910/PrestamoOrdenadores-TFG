package org.example.prestamoordenadores.storage.pdf

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.storage.csv.IncidenciaCsvStorage
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class IncidenciaPdfStorageTest {

    @MockK
    lateinit var incidenciaRepository: IncidenciaRepository

    @MockK
    lateinit var dispositivoRepository: DispositivoRepository

    lateinit var storage : IncidenciaPdfStorage

    var user = User()
    var dispositivo = Dispositivo()
    var incidencia = Incidencia()

    @BeforeEach
    fun setUp() {
        user = User(
            id = 99,
            guid = "guidTestU99",
            email = "email99@loantech.com",
            numeroIdentificacion = "2023LT249",
            campoPassword = "Password123?",
            nombre = "nombre99",
            apellidos = "apellidos99",
            curso = "curso99",
            tutor = "tutor99",
            avatar = "avatar99.png",
            rol = Role.ALUMNO,
            isActivo = true,
            lastLoginDate = LocalDateTime.now(),
            lastPasswordResetDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        incidencia = Incidencia(
            id = 1,
            guid = "INC000000",
            asunto = "asunto",
            descripcion = "descripcion",
            estadoIncidencia = EstadoIncidencia.PENDIENTE,
            user = user,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        dispositivo = Dispositivo(
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

        storage = IncidenciaPdfStorage(incidenciaRepository, dispositivoRepository)
    }

    @Test
    fun generatePdf() {
        every { incidenciaRepository.findIncidenciaByGuid(incidencia.guid) } returns incidencia
        every { dispositivoRepository.findDispositivoByIncidenciaGuid(incidencia.guid) } returns dispositivo

        val pdfBytes = storage.generatePdf(incidencia.guid)

        assertAll(
            { assertNotNull(pdfBytes) },
            { assertTrue(pdfBytes.isNotEmpty()) },
            { verify { incidenciaRepository.findIncidenciaByGuid(incidencia.guid) } },
            { verify { dispositivoRepository.findDispositivoByIncidenciaGuid(incidencia.guid) } }
        )
    }

    @Test
    fun savePdfToFile() {
        val content = "PDF CONTENT".toByteArray()
        val filename = "incidencia_${LocalDate.now().toDefaultDateString()}.pdf"
        val path = "data/$filename"
        val file = File(path)

        if (file.exists()) file.delete()

        storage.savePdfToFile(content, filename)

        assertAll(
            { assertTrue(file.exists()) },
            { assertTrue(file.readBytes().contentEquals(content)) }
        )

        file.delete()
    }

    @Test
    fun generateAndSavePdf() {
        val mockStorage = spyk(IncidenciaPdfStorage(incidenciaRepository, dispositivoRepository))
        val pdfData = "dummy".toByteArray()
        val expectedFilename = "incidencia_${LocalDate.now().toDefaultDateString()}.pdf"

        every { mockStorage.generatePdf(incidencia.guid) } returns pdfData
        every { mockStorage.savePdfToFile(pdfData, expectedFilename) } just Runs

        mockStorage.generateAndSavePdf(incidencia.guid)

        verify { mockStorage.generatePdf(incidencia.guid) }
        verify { mockStorage.savePdfToFile(pdfData, expectedFilename) }
    }

}