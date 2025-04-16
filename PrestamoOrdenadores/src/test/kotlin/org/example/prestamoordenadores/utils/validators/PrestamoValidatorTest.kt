package org.example.prestamoordenadores.utils.validators

import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrestamoValidatorTest {

    @Test
    fun validate() {
        val request = PrestamoUpdateRequest(
            estado = "VENCIDO"
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate Error when estado es blank`() {
        val request = PrestamoUpdateRequest(
            estado = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El estado del préstamo no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate Error when estado invalido`() {
        val request = PrestamoUpdateRequest(
            estado = "ESTADO_INVALIDO"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El estado del préstamo no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate PrestamoUpdateRequest with lowercase valid estado returns Ok`() {
        val request = PrestamoUpdateRequest(
            estado = EstadoPrestamo.VENCIDO.name.lowercase()
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }
}