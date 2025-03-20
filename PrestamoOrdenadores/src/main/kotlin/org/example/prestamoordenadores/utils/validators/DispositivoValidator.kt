package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError

fun DispositivoCreateRequest.validate(): Result<DispositivoCreateRequest, DispositivoError>  {
    //val regexEmail = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    if (this.componentes.isBlank()) {
        return Err(DispositivoError.DispositivoValidationError("Los componentes no pueden estar vacíos"))
    } else if (this.stock <= 0)
        return Err(DispositivoError.DispositivoValidationError("El stock no puede ser menor o igual a 0"))

    return Ok(this)
}