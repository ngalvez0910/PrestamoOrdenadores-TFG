package org.example.prestamoordenadores.utils.validators

import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SancionValidatorTest {

    @Test
    fun validateSancionRequest() {
        val request = SancionRequest(
            userGuid = "user-123",
            tipoSancion = TipoSancion.BLOQUEO_TEMPORAL.name
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate SancionRequest Error when userGuid es blank`() {
        val request = SancionRequest(
            userGuid = "",
            tipoSancion = TipoSancion.BLOQUEO_TEMPORAL.name
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El guid del usuario no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate SancionRequest Error when tipoSancion es blank`() {
        val request = SancionRequest(
            userGuid = "user-123",
            tipoSancion = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El tipo de la sanción no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate SancionRequest Error when tipoSancion invalido`() {
        val request = SancionRequest(
            userGuid = "user-123",
            tipoSancion = "TIPO_INVALIDO"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El tipo de la sanción no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate SancionRequest when tipoSancion en lowercase`() {
        val request = SancionRequest(
            userGuid = "user-123",
            tipoSancion = TipoSancion.ADVERTENCIA.name.lowercase()
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun validateSancionUpdateRequest() {
        val request = SancionUpdateRequest(
            tipo = TipoSancion.BLOQUEO_TEMPORAL.name
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate SancionUpdateRequest Error when tipoSancion es blank`() {
        val request = SancionUpdateRequest(
            tipo = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El tipo de la sanción no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate SancionUpdateRequest Error when tipoSancion invalido`() {
        val request = SancionUpdateRequest(
            tipo = "TIPO_INVALIDO"
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El tipo de la sanción no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate SancionUpdateRequest when tipoSancion en lowercase`() {
        val request = SancionUpdateRequest(
            tipo = TipoSancion.ADVERTENCIA.name.lowercase()
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }
}