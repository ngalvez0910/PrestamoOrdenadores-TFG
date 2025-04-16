package org.example.prestamoordenadores.utils.validators

import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaCreateRequest
import org.example.prestamoordenadores.rest.incidencias.dto.IncidenciaUpdateRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IncidenciaValidatorTest {

    @Test
    fun validateIncidenciaCreateRequest() {
        val request = IncidenciaCreateRequest(
            asunto = "Problema con el teclado",
            descripcion = "Algunas teclas no responden correctamente."
        )

        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate IncidenciaCreateRequest Error when asunto es blank`() {
        val request = IncidenciaCreateRequest(
            asunto = "",
            descripcion = "Algunas teclas no responden correctamente."
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El asunto no puede estar vacío", result.error.message)
    }

    @Test
    fun `validate IncidenciaCreateRequest Error when descripcion es blank `() {
        val request = IncidenciaCreateRequest(
            asunto = "Problema con el teclado",
            descripcion = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("La descripcion no puede estar vacía", result.error.message)
    }

    @Test
    fun validateIncidenciaUpdateRequest() {
        val request = IncidenciaUpdateRequest(
            estadoIncidencia = "EN_PROGRESO"
        )
        val result = request.validate()
        assertTrue(result.isOk)
        assertEquals(request, result.component1())
    }

    @Test
    fun `validate IncidenciaUpdateRequest Error when estadoIncidencia es blank`() {
        val request = IncidenciaUpdateRequest(
            estadoIncidencia = ""
        )
        val result = request.validate()
        assertTrue(result.isErr)
        assertEquals("El estado de la incidencia no puede estar vacío", result.error.message)
    }
}