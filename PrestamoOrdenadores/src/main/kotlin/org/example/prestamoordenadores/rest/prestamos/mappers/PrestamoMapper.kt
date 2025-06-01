package org.example.prestamoordenadores.rest.prestamos.mappers

import org.example.prestamoordenadores.rest.dispositivos.mappers.DispositivoMapper
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponse
import org.example.prestamoordenadores.rest.prestamos.dto.PrestamoResponseAdmin
import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.locale.toDefaultDateString
import org.example.prestamoordenadores.utils.locale.toDefaultDateTimeString
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Mapper para la conversión entre entidades [Prestamo] y sus DTOs.
 *
 * Esta clase se encarga de transformar objetos de solicitud y entidades de base de datos
 * en objetos de respuesta adecuados para la API, y viceversa.
 *
 * @author Natalia González Álvarez
 */
@Component
class PrestamoMapper {
    /**
     * Convierte una entidad [Prestamo] a un [PrestamoResponse] básico.
     *
     * @param prestamo La entidad [Prestamo] a convertir.
     * @return Un [PrestamoResponse] con los datos principales del préstamo.
     * @author Natalia González Álvarez
     */
    fun toPrestamoResponse(prestamo: Prestamo): PrestamoResponse {
        val userMapper = UserMapper() // Instancia un UserMapper para mapear el usuario asociado
        val dispositivoMapper = DispositivoMapper() // Instancia un DispositivoMapper para mapear el dispositivo asociado

        return PrestamoResponse(
            guid = prestamo.guid,
            user = userMapper.toUserResponse(prestamo.user), // Mapea el usuario a su DTO de respuesta
            dispositivo = dispositivoMapper.toDispositivoResponse(prestamo.dispositivo), // Mapea el dispositivo a su DTO de respuesta
            estadoPrestamo = prestamo.estadoPrestamo.toString(),
            fechaPrestamo = prestamo.fechaPrestamo.toDefaultDateString(), // Formatea la fecha de préstamo
            fechaDevolucion = prestamo.fechaDevolucion.toDefaultDateString() // Formatea la fecha de devolución
        )
    }

    /**
     * Convierte una entidad [Prestamo] a un [PrestamoResponseAdmin].
     *
     * Incluye información adicional como fechas de creación y actualización, y el estado de eliminación lógica,
     * adecuada para vistas administrativas.
     *
     * @param prestamo La entidad [Prestamo] a convertir.
     * @return Un [PrestamoResponseAdmin] con todos los detalles del préstamo.
     * @author Natalia González Álvarez
     */
    fun toPrestamoResponseAdmin(prestamo: Prestamo): PrestamoResponseAdmin {
        val userMapper = UserMapper() // Instancia un UserMapper para mapear el usuario asociado
        val dispositivoMapper = DispositivoMapper() // Instancia un DispositivoMapper para mapear el dispositivo asociado

        return PrestamoResponseAdmin(
            guid = prestamo.guid,
            user = userMapper.toUserResponse(prestamo.user), // Mapea el usuario a su DTO de respuesta
            dispositivo = dispositivoMapper.toDispositivoResponse(prestamo.dispositivo), // Mapea el dispositivo a su DTO de respuesta
            estadoPrestamo = prestamo.estadoPrestamo.toString(),
            fechaPrestamo = prestamo.fechaPrestamo.toDefaultDateString(), // Formatea la fecha de préstamo
            fechaDevolucion = prestamo.fechaDevolucion.toDefaultDateString(), // Formatea la fecha de devolución
            createdDate = prestamo.createdDate.toDefaultDateTimeString(), // Formatea la fecha de creación
            updatedDate = prestamo.updatedDate.toDefaultDateTimeString(), // Formatea la fecha de última actualización
            isDeleted = prestamo.isDeleted
        )
    }

    /**
     * Convierte un [User] y un [Dispositivo] a una entidad [Prestamo] para su creación.
     *
     * Establece el estado inicial del préstamo como [EstadoPrestamo.EN_CURSO]
     * y las fechas de préstamo, creación y actualización al momento actual.
     *
     * @param user El [User] que realiza el préstamo.
     * @param dispositivo El [Dispositivo] que se presta.
     * @return Una entidad [Prestamo] lista para ser persistida.
     * @author Natalia González Álvarez
     */
    fun toPrestamoFromCreate(user: User, dispositivo: Dispositivo): Prestamo {
        return Prestamo(
            user = user,
            dispositivo = dispositivo,
            estadoPrestamo = EstadoPrestamo.EN_CURSO, // Estado inicial del préstamo
            fechaPrestamo = LocalDate.now(), // Fecha de préstamo actual
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    /**
     * Convierte una lista de entidades [Prestamo] a una lista de [PrestamoResponse] básicos.
     *
     * @param prestamos La lista de entidades [Prestamo] a convertir.
     * @return Una lista de [PrestamoResponse].
     * @author Natalia González Álvarez
     */
    fun toPrestamoResponseList(prestamos: List<Prestamo>): List<PrestamoResponse> {
        return prestamos.map { toPrestamoResponse(it) }
    }
}