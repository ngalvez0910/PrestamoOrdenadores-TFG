package org.example.prestamoordenadores.rest.prestamos.dto

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponse

data class PrestamoResponseAdmin(
    var guid: String,
    var user: UserResponse,
    var dispositivo: DispositivoResponse,
    var estadoPrestamo: String,
    var fechaPrestamo: String,
    var fechaDevolucion: String?,
    var createdDate: String,
    var updatedDate: String,
    var isDeleted: Boolean
)
