package org.example.prestamoordenadores.utils.validators

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DispositivoValidatorTest {

    @Test
    fun validate() {
        val request = DispositivoCreateRequest(
            numeroSerie = "4JF857MALK",
            componentes = "RAM 16GB, SSD 512GB"
        )

        val result = request.validate()

        assertTrue(result.isOk)
        assertEquals(request, result.value)
    }

    @Test
    fun `validate Error when componentes es blank`() {
        val request = DispositivoCreateRequest(
            numeroSerie = "4JF857MALK",
            componentes = ""
        )

        val result = request.validate()

        assertTrue(result.isErr)
        assertTrue(result.error is DispositivoError.DispositivoValidationError)
        assertEquals("Los componentes no pueden estar vacíos", result.error.message)
    }

    @Test
    fun `validate Error when componentes contiene solo espacios en blanco`() {
        val request = DispositivoCreateRequest(
            numeroSerie = "4JF857MALK",
            componentes = "   "
        )

        val result = request.validate()

        assertTrue(result.isErr)
        assertTrue(result.error is DispositivoError.DispositivoValidationError)
        assertEquals("Los componentes no pueden estar vacíos", result.error.message)
    }

    @Test
    fun `validate Error when numeroSerie es blank`() {
        val request = DispositivoCreateRequest(
            numeroSerie = "",
            componentes = "RAM 16GB"
        )

        val result = request.validate()

        assertTrue(result.isErr)
        assertTrue(result.error is DispositivoError.DispositivoValidationError)
        assertEquals("Número de serie inválido", result.error.message)
    }

    @Test
    fun `validate Error when numeroSerie tiene formato inválido`() {
        val request = DispositivoCreateRequest(
            numeroSerie = "INVALID123",
            componentes = "RAM 8GB"
        )

        val result = request.validate()

        assertTrue(result.isErr)
        assertTrue(result.error is DispositivoError.DispositivoValidationError)
        assertEquals("Número de serie inválido", result.error.message)
    }

}