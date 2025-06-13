package org.example.prestamoordenadores.storage.pdf

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll
import java.io.File

@ExtendWith(MockKExtension::class)
class PrestamoPdfStorageTest {

    @MockK
    lateinit var prestamoRepository: PrestamoRepository

    @MockK
    lateinit var dispositivoRepository: DispositivoRepository

    @MockK
    lateinit var userRepository: UserRepository

    lateinit var storage : PrestamoPdfStorage

    var user = User()
    var dispositivo = Dispositivo()
    var prestamo = Prestamo()

    @BeforeEach
    fun setUp() {
        user = User(
            id = 99,
            guid = "guidTestU99",
            email = "email99.loantech@gmail.com",
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

        prestamo = Prestamo(
            id = 1,
            guid = "guidTestP01",
            user = user,
            dispositivo = dispositivo,
            estadoPrestamo = EstadoPrestamo.EN_CURSO,
            fechaPrestamo = LocalDate.now(),
            fechaDevolucion = LocalDate.now().plusDays(1),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        storage = PrestamoPdfStorage(prestamoRepository, userRepository, dispositivoRepository)
    }

    @Test
    fun generatePdf() {
        every { prestamoRepository.findByGuid(prestamo.guid) } returns prestamo
        every { userRepository.findByGuid(user.guid) } returns user
        every { dispositivoRepository.findDispositivoByGuid(dispositivo.guid) } returns dispositivo

        val pdfBytes = storage.generatePdf(prestamo.guid)

        assertAll(
            { assertNotNull(pdfBytes) },
            { assertTrue(pdfBytes.isNotEmpty()) },
            { verify { prestamoRepository.findByGuid(prestamo.guid) } },
            { verify { userRepository.findByGuid(user.guid) } },
            { verify { dispositivoRepository.findDispositivoByGuid(dispositivo.guid) } }
        )
    }

    @Test
    fun savePdfToFile() {
        val tempFileName = "prestamo_${LocalDate.now().toDefaultDateString()}.pdf"
        val pdfData = "Dummy PDF content".toByteArray()

        val path = File("data/$tempFileName")
        if (path.exists()) path.delete()

        storage.savePdfToFile(pdfData, tempFileName)

        assertAll(
            { assertTrue(path.exists()) },
            { assertTrue(path.readBytes().contentEquals(pdfData)) }
        )

        path.delete()
    }

    @Test
    fun generateAndSavePdf() {
        val filename = "prestamo_${LocalDate.now().toDefaultDateString()}.pdf"
        val path = File("data/$filename")

        every { prestamoRepository.findByGuid(prestamo.guid) } returns prestamo
        every { userRepository.findByGuid(user.guid) } returns user
        every { dispositivoRepository.findDispositivoByGuid(dispositivo.guid) } returns dispositivo

        storage.generateAndSavePdf(prestamo.guid)

        assertAll(
            { assertTrue(path.exists()) },
            { assertTrue(path.readBytes().isNotEmpty()) },
            { verify { prestamoRepository.findByGuid(prestamo.guid) } },
            { verify { userRepository.findByGuid(user.guid) } },
            { verify { dispositivoRepository.findDispositivoByGuid(dispositivo.guid) } }
        )

        path.delete()
    }
}