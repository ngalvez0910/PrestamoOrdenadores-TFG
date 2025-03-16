package org.example.prestamoordenadores.rest.prestamos.mappers

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoCreateRequest
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
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
            fechaPrestamo = prestamo.fechaPrestamo.toDefaultDateString(),
            fechaDevolucion = prestamo.fechaDevolucion.toDefaultDateString()
        )
    }

    fun toPrestamoFromCreate(prestamo: PrestamoCreateRequest, dispositivo: Dispositivo): Prestamo {
        return Prestamo(
            userGuid = prestamo.userGuid,
            dispositivoGuid = dispositivo.guid,
            estadoPrestamo = EstadoPrestamo.EN_CURSO,
            fechaPrestamo = LocalDate.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toPrestamoResponseList(prestamos: List<Prestamo?>): List<PrestamoResponse> {
        return prestamos.map { toPrestamoResponse(it!!) }
    }
}