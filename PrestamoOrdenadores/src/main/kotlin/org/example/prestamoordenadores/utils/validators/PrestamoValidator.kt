package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError

fun PrestamoUpdateRequest.validate(): Result<PrestamoUpdateRequest, PrestamoError> {
    if (this.estado.isBlank()) {
        return Err(PrestamoError.PrestamoValidationError("El estado del préstamo no puede estar vacío"))
    }

    return Ok(this)
}