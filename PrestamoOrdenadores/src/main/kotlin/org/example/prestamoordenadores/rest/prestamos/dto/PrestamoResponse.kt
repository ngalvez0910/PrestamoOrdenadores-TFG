package org.example.prestamoordenadores.rest.prestamos.dto

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.users.dto.UserResponse

data class PrestamoResponse (
    var guid: String,
    var user: UserResponse,
    var dispositivo: DispositivoResponse,
    var fechaPrestamo: String,
    var fechaDevolucion: String?
)