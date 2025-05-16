package org.example.prestamoordenadores.rest.sanciones.mappers

import org.example.prestamoordenadores.rest.prestamos.mappers.PrestamoMapper
import org.example.prestamoordenadores.rest.sanciones.dto.SancionAdminResponse
import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class SancionMapper {
    fun toSancionResponse(sancion: Sancion) : SancionResponse{
        val userMapper = UserMapper()
        val prestamoMapper = PrestamoMapper()

        return SancionResponse(
            guid = sancion.guid,
            user = userMapper.toUserResponse(sancion.user),
            prestamo = prestamoMapper.toPrestamoResponse(sancion.prestamo),
            tipoSancion = sancion.tipoSancion.toString(),
            fechaSancion = sancion.fechaSancion.toDefaultDateString(),
            fechaFin = sancion.fechaFin?.toDefaultDateString() ?: LocalDate.now().toDefaultDateString()
        )
    }

    fun toSancionAdminResponse(sancion: Sancion) : SancionAdminResponse{
        val userMapper = UserMapper()
        val prestamoMapper = PrestamoMapper()

        return SancionAdminResponse(
            guid = sancion.guid,
            user = userMapper.toUserResponse(sancion.user),
            prestamo = prestamoMapper.toPrestamoResponse(sancion.prestamo),
            tipoSancion = sancion.tipoSancion.toString(),
            fechaSancion = sancion.fechaSancion.toDefaultDateString(),
            fechaFin = sancion.fechaFin?.toDefaultDateString() ?: LocalDate.now().toDefaultDateString(),
            createdDate = sancion.createdDate.toDefaultDateTimeString(),
            updatedDate = sancion.updatedDate.toDefaultDateTimeString(),
            isDeleted = sancion.isDeleted
        )
    }

    fun toSancionFromRequest(sancion: SancionRequest, userRepository: UserRepository): Sancion {
        val user: User? = userRepository.findByGuid(sancion.userGuid)

        if (user == null) {
            throw IllegalArgumentException("Usuario con GUID ${sancion.userGuid} no encontrado")
        }

        return Sancion(
            user = user,
            tipoSancion = TipoSancion.valueOf(sancion.tipoSancion),
            fechaSancion = LocalDate.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toSancionResponseList(sanciones: List<Sancion?>): List<SancionResponse> {
        return sanciones.map { toSancionResponse(it!!) }
    }

    fun toSancionAdminResponseList(sanciones: List<Sancion?>): List<SancionAdminResponse> {
        return sanciones.map { toSancionAdminResponse(it!!) }
    }
}