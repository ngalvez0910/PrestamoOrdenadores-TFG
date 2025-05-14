package org.example.prestamoordenadores.utils.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.errors.DispositivoError

fun DispositivoCreateRequest.validate(): Result<DispositivoCreateRequest, DispositivoError>  {
    return when{
        numeroSerie.isBlank() || !numeroSerie.matches(Regex("^\\d[A-Z]{2}\\d{3}[A-Z]{4}\$")) ->
            Err(DispositivoError.DispositivoValidationError("Número de serie inválido"))

        componentes.isBlank() ->
            Err(DispositivoError.DispositivoValidationError("Los componentes no pueden estar vacíos"))
        else -> Ok(this)
    }
}