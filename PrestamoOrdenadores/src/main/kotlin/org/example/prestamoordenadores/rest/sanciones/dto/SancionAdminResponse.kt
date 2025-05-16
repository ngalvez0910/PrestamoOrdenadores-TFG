package org.example.prestamoordenadores.rest.sanciones.dto

import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponse

data class SancionAdminResponse(
    val guid : String,
    val user : UserResponse,
    val prestamo: PrestamoResponse,
    val tipoSancion : String,
    val fechaSancion : String,
    val fechaFin : String,
    var createdDate: String,
    var updatedDate: String,
    var isDeleted: Boolean
)
