package org.example.prestamoordenadores.rest.prestamos.mappers

import org.example.prestamoordenadores.rest.dispositivos.mappers.DispositivoMapper
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class PrestamoMapper {
    fun toPrestamoResponse(prestamo: Prestamo): PrestamoResponse{
        val userMapper = UserMapper()
        val dispositivoMapper = DispositivoMapper()

        return PrestamoResponse(
            guid = prestamo.guid,
            user = userMapper.toUserResponse(prestamo.user),
            dispositivo = dispositivoMapper.toDispositivoResponse(prestamo.dispositivo),
            fechaPrestamo = prestamo.fechaPrestamo.toDefaultDateString(),
            fechaDevolucion = prestamo.fechaDevolucion.toDefaultDateString()
        )
    }

    fun toPrestamoFromCreate(user: User, dispositivo: Dispositivo): Prestamo {
        return Prestamo(
            user = user,
            dispositivo = dispositivo,
            estadoPrestamo = EstadoPrestamo.EN_CURSO,
            fechaPrestamo = LocalDate.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toPrestamoResponseList(prestamos: List<Prestamo>): List<PrestamoResponse> {
        return prestamos.map { toPrestamoResponse(it) }
    }
}