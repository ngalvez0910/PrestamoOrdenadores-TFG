package org.example.prestamoordenadores.rest.prestamos.dto

data class PrestamoResponse (
    var guid: String,
    var userGuid: String,
    var dispositivoGuid: String,
    var fechaPrestamo: String,
    var fechaDevolucion: String?
)