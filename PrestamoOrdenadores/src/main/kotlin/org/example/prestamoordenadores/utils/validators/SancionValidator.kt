package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionUpdateRequest
import org.example.prestamoordenadores.rest.sanciones.errors.SancionError

fun SancionRequest.validate(): Result<SancionRequest, SancionError> {
    if (this.userGuid.isBlank()) {
        return Err(SancionError.SancionValidationError("El guid del usuario no puede estar vacío"))
    } else if (this.tipoSancion.isBlank()) {
        return Err(SancionError.SancionValidationError("El tipo de la sanción no puede estar vacío"))
    }

    return Ok(this)
}

fun SancionUpdateRequest.validate(): Result<SancionUpdateRequest, SancionError> {
    if (this.tipo.isBlank()) {
        return Err(SancionError.SancionValidationError("El tipo de la sanción no puede estar vacío"))
    }

    return Ok(this)
}