package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError

fun DispositivoCreateRequest.validate(): Result<DispositivoCreateRequest, DispositivoError>  {
    if (this.componentes.isBlank()) {
        return Err(DispositivoError.DispositivoValidationError("Los componentes no pueden estar vacíos"))
    }

    return Ok(this)
}