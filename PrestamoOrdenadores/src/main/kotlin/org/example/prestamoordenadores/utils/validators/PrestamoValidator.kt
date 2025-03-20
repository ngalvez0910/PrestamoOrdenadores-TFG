package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoUpdateRequest
import org.example.prestamoordenadores.rest.prestamos.errors.PrestamoError

fun PrestamoCreateRequest.validate(): Result<PrestamoCreateRequest, PrestamoError> {
    if (this.userGuid.isBlank()) {
        return Err(PrestamoError.PrestamoValidationError("El guid del usuario es inválido"))
    }

    return Ok(this)
}

fun PrestamoUpdateRequest.validate(): Result<PrestamoUpdateRequest, PrestamoError> {
    if (this.estado.isBlank()) {
        return Err(PrestamoError.PrestamoValidationError("El estado del préstamo no puede estar vacío"))
    }

    return Ok(this)
}