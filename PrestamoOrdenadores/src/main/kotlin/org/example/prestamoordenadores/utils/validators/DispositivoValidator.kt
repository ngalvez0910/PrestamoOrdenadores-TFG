package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError

/**
 * Función de extensión para validar un objeto [DispositivoCreateRequest].
 *
 * Realiza las siguientes validaciones:
 * - El número de serie no puede estar en blanco.
 * - El número de serie debe seguir el patrón "1XX123XXXX" (un dígito, dos letras mayúsculas, tres dígitos, cuatro letras mayúsculas).
 * - Los componentes no pueden estar en blanco.
 *
 * @receiver La instancia de [DispositivoCreateRequest] a validar.
 * @return Un [Result] que contiene el [DispositivoCreateRequest] validado si la validación es exitosa ([Ok]),
 * o un [DispositivoError.DispositivoValidationError] si falla ([Err]).
 * @author Natalia González Álvarez
 */
fun DispositivoCreateRequest.validate(): Result<DispositivoCreateRequest, DispositivoError> {
    val regexNumSerie = Regex("^\\d[A-Z]{2}\\d{3}[A-Z]{4}$")

    if (this.numeroSerie.isBlank()) {
        return Err(DispositivoError.DispositivoValidationError("El número de serie no puede estar vacío"))
    }

    if (!this.numeroSerie.matches(regexNumSerie)) {
        return Err(DispositivoError.DispositivoValidationError("El formato del número de serie no es válido. Debe seguir el patrón: 1XX123XXXX"))
    }

    if (this.componentes.isBlank()) {
        return Err(DispositivoError.DispositivoValidationError("Los componentes no pueden estar vacíos"))
    }

    return Ok(this)
}