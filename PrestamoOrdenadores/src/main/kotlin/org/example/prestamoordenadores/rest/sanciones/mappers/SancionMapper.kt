package org.example.prestamoordenadores.rest.sanciones.mappers

import org.example.prestamoordenadores.rest.sanciones.dto.SancionRequest
import org.example.prestamoordenadores.rest.sanciones.dto.SancionResponse
import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class SancionMapper {
    fun toSancionResponse(sancion: Sancion) : SancionResponse{
        return SancionResponse(
            guid = sancion.guid,
            userGuid = sancion.userGuid,
            tipoSancion = sancion.tipoSancion.toString(),
            fechaSancion = sancion.fechaSancion.toDefaultDateString()
        )
    }

    fun toSancionFromRequest(sancion: SancionRequest) : Sancion{
        return Sancion(
            userGuid = sancion.userGuid,
            tipoSancion = TipoSancion.valueOf(sancion.tipoSancion),
            fechaSancion = LocalDate.now(),
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    fun toSancionResponseList(sanciones: List<Sancion?>): List<SancionResponse> {
        return sanciones.map { toSancionResponse(it!!) }
    }
}