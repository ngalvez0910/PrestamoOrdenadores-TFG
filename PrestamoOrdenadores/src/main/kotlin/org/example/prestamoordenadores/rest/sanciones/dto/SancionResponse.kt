package org.example.prestamoordenadores.rest.sanciones.dto

import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponse

data class SancionResponse(
    val guid : String,
    val user : UserResponse,
    val prestamo: PrestamoResponse,
    val tipoSancion : String,
    val fechaSancion : String,
    val fechaFin : String
)
