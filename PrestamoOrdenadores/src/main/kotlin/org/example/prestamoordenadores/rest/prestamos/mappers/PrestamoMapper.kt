package org.example.prestamoordenadores.rest.prestamos.mappers

import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.Estado
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

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

    fun toPrestamoFromCreate(prestamo: PrestamoCreateRequest): Prestamo {
        return Prestamo(
            userGuid = prestamo.userGuid,
            dispositivoGuid = prestamo.dispositivoGuid,
            estado = Estado.EN_CURSO,
            fechaPrestamo = LocalDate.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toPrestamoResponseList(prestamos: List<Prestamo?>): List<PrestamoResponse> {
        return prestamos.map { toPrestamoResponse(it!!) }
    }
}