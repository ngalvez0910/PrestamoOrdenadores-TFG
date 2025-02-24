package org.example.prestamoordenadores.rest.prestamos.mappers

import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.springframework.stereotype.Component

@Component
class PrestamoMapper {
    fun toPrestamoResponse(prestamo: Prestamo): PrestamoResponse{
        return PrestamoResponse(
            guid = prestamo.guid,
            userGuid = prestamo.userGuid,
            dispositivoGuid = prestamo.dispositivoGuid,
            fechaPrestamo = prestamo.fechaPrestamo.toString(),
            fechaDevolucion = prestamo.fechaDevolucion.toString()
        )
    }

    fun toPrestamoResponseList(prestamos: List<Prestamo?>): List<PrestamoResponse> {
        return prestamos.map { toPrestamoResponse(it!!) }
    }
}