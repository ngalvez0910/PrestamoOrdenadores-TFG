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

/**
 * Mapper para la conversión entre entidades [Sancion] y sus DTOs.
 *
 * Esta clase se encarga de transformar objetos de solicitud y entidades de base de datos
 * en objetos de respuesta adecuados para la API, y viceversa.
 *
 * @author Natalia González Álvarez
 */
@Component
class SancionMapper {
    /**
     * Convierte una entidad [Sancion] a un [SancionResponse] básico.
     *
     * @param sancion La entidad [Sancion] a convertir.
     * @return Un [SancionResponse] con los datos principales de la sanción.
     * @author Natalia González Álvarez
     */
    fun toSancionResponse(sancion: Sancion): SancionResponse {
        val userMapper = UserMapper() // Instancia un UserMapper para mapear el usuario asociado
        val prestamoMapper = PrestamoMapper() // Instancia un PrestamoMapper para mapear el préstamo asociado

        return SancionResponse(
            guid = sancion.guid,
            user = userMapper.toUserResponse(sancion.user), // Mapea el usuario a su DTO de respuesta
            prestamo = prestamoMapper.toPrestamoResponse(sancion.prestamo), // Mapea el préstamo a su DTO de respuesta
            tipoSancion = sancion.tipoSancion.toString(),
            fechaSancion = sancion.fechaSancion.toDefaultDateString(), // Formatea la fecha de sanción
            fechaFin = sancion.fechaFin?.toDefaultDateString() ?: LocalDate.now().toDefaultDateString() // Formatea la fecha de fin o usa la actual si es nula
        )
    }

    /**
     * Convierte una entidad [Sancion] a un [SancionAdminResponse].
     *
     * Incluye información adicional como fechas de creación y actualización, y el estado de eliminación lógica,
     * adecuada para vistas administrativas.
     *
     * @param sancion La entidad [Sancion] a convertir.
     * @return Un [SancionAdminResponse] con todos los detalles de la sanción.
     * @author Natalia González Álvarez
     */
    fun toSancionAdminResponse(sancion: Sancion): SancionAdminResponse {
        val userMapper = UserMapper() // Instancia un UserMapper para mapear el usuario asociado
        val prestamoMapper = PrestamoMapper() // Instancia un PrestamoMapper para mapear el préstamo asociado

        return SancionAdminResponse(
            guid = sancion.guid,
            user = userMapper.toUserResponse(sancion.user), // Mapea el usuario a su DTO de respuesta
            prestamo = prestamoMapper.toPrestamoResponse(sancion.prestamo), // Mapea el préstamo a su DTO de respuesta
            tipoSancion = sancion.tipoSancion.toString(),
            fechaSancion = sancion.fechaSancion.toDefaultDateString(), // Formatea la fecha de sanción
            fechaFin = sancion.fechaFin?.toDefaultDateString() ?: LocalDate.now().toDefaultDateString(), // Formatea la fecha de fin o usa la actual si es nula
            createdDate = sancion.createdDate.toDefaultDateTimeString(), // Formatea la fecha de creación
            updatedDate = sancion.updatedDate.toDefaultDateTimeString(), // Formatea la fecha de última actualización
            isDeleted = sancion.isDeleted
        )
    }

    /**
     * Convierte un [SancionRequest] a una entidad [Sancion] para su creación.
     *
     * Busca el usuario por su GUID y establece las fechas de sanción, creación y actualización al momento actual.
     *
     * @param sancion El [SancionRequest] con los datos para la nueva sanción.
     * @param userRepository El repositorio de usuarios para buscar el usuario por GUID.
     * @return Una entidad [Sancion] lista para ser persistida.
     * @throws IllegalArgumentException si el usuario con el GUID especificado no es encontrado.
     * @author Natalia González Álvarez
     */
    fun toSancionFromRequest(sancion: SancionRequest, userRepository: UserRepository): Sancion {
        val user: User? = userRepository.findByGuid(sancion.userGuid)

        if (user == null) {
            throw IllegalArgumentException("Usuario con GUID ${sancion.userGuid} no encontrado")
        }

        return Sancion(
            user = user,
            tipoSancion = TipoSancion.valueOf(sancion.tipoSancion), // Convierte la cadena a TipoSancion
            fechaSancion = LocalDate.now(), // Fecha de sanción actual
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    /**
     * Convierte una lista de entidades [Sancion] a una lista de [SancionResponse] básicos.
     *
     * @param sanciones La lista de entidades [Sancion] a convertir.
     * @return Una lista de [SancionResponse]. Se asume que las entidades no son nulas.
     * @author Natalia González Álvarez
     */
    fun toSancionResponseList(sanciones: List<Sancion?>): List<SancionResponse> {
        return sanciones.map { toSancionResponse(it!!) } // Usa !! para asumir que no hay nulos en la lista
    }
}