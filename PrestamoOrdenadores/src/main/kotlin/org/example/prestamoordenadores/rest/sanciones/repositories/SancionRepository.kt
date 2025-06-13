package org.example.prestamoordenadores.rest.sanciones.repositories

import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Repositorio para la entidad [Sancion].
 *
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre las sanciones en la base de datos.
 *
 * @author Natalia González Álvarez
 */
@Repository
interface SancionRepository : JpaRepository<Sancion, Long> {
    /**
     * Busca una sanción por su identificador único global (GUID).
     * @param guid El GUID de la sanción a buscar.
     * @return La [Sancion] encontrada, o `null` si no existe.
     * @author Natalia González Álvarez
     */
    fun findByGuid(guid: String): Sancion?

    /**
     * Busca una lista de sanciones por su fecha de aplicación.
     * @param fecha La fecha de sanción a buscar.
     * @return Una lista de [Sancion] que coinciden con la fecha de sanción.
     * @author Natalia González Álvarez
     */
    fun findSancionByFechaSancion(fecha: LocalDate): List<Sancion>

    /**
     * Busca una lista de sanciones por su tipo.
     * @param tipo El [TipoSancion] de las sanciones a buscar.
     * @return Una lista de [Sancion] que coinciden con el tipo de sanción.
     * @author Natalia González Álvarez
     */
    fun findSancionByTipoSancion(tipo: TipoSancion): List<Sancion>

    /**
     * Busca una lista de sanciones asociadas a un usuario por su GUID.
     * @param userGuid El GUID del usuario cuyas sanciones se desean buscar.
     * @return Una lista de [Sancion] aplicadas al usuario especificado.
     * @author Natalia González Álvarez
     */
    fun findByUserGuid(userGuid: String): List<Sancion>

    /**
     * Busca una lista de sanciones aplicadas a un usuario específico y de un tipo determinado.
     * @param user El [User] al que se le aplicó la sanción.
     * @param tipoSancion El [TipoSancion] de la sanción a buscar.
     * @return Una lista de [Sancion] que coinciden con el usuario y el tipo de sanción.
     * @author Natalia González Álvarez
     */
    fun findByUserAndTipoSancion(user: User, tipoSancion: TipoSancion): List<Sancion>

    /**
     * Verifica la existencia de una sanción para un préstamo específico y un tipo de sanción dado.
     * @param prestamoGuid El GUID del préstamo asociado a la sanción.
     * @param tipoSancion El [TipoSancion] a verificar.
     * @return `true` si existe una sanción que cumple con los criterios, `false` en caso contrario.
     * @author Natalia González Álvarez
     */
    fun existsByPrestamoGuidAndTipoSancion(prestamoGuid: String, tipoSancion: TipoSancion): Boolean

    /**
     * Busca sanciones de un tipo específico cuya fecha de fin es igual o anterior a la fecha dada,
     * y cuyo usuario asociado no está activo (`isActivo` es `false`).
     * @param tipoSancion El [TipoSancion] de la sanción.
     * @param fecha La fecha límite para la finalización de la sanción.
     * @return Una lista de [Sancion] que cumplen con los criterios.
     * @author Natalia González Álvarez
     */
    fun findByTipoSancionAndFechaFinLessThanEqualAndUserIsActivoIsFalse(tipoSancion: TipoSancion, fecha: LocalDate): List<Sancion>

    /**
     * Busca sanciones aplicadas a un usuario específico y que pertenecen a una lista de tipos de sanción.
     * @param user El [User] al que se le aplicaron las sanciones.
     * @param tipos Una lista de [TipoSancion] a buscar.
     * @return Una lista de [Sancion] que coinciden con el usuario y alguno de los tipos de sanción especificados.
     * @author Natalia González Álvarez
     */
    fun findSancionsByUserAndTipoSancionIn(user: User, tipos: List<TipoSancion>): List<Sancion>

    /**
     * Busca una lista de sanciones asociadas a un usuario por su ID.
     * @param userId El ID del usuario cuyas sanciones se desean buscar.
     * @return Una lista de [Sancion] aplicadas al usuario especificado. Puede contener `null` si una sanción no tiene un usuario asociado válido.
     * @author Natalia González Álvarez
     */
    fun findSancionsByUserId(userId: Long): List<Sancion?>
}