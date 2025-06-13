package org.example.prestamoordenadores.rest.prestamos.repositories

import org.example.prestamoordenadores.rest.prestamos.models.EstadoPrestamo
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Repositorio para la entidad [Prestamo].
 *
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre los préstamos en la base de datos.
 *
 * @author Natalia González Álvarez
 */
@Repository
interface PrestamoRepository : JpaRepository<Prestamo, Long> {
    /**
     * Busca un préstamo por su identificador único global (GUID).
     * @param guid El GUID del préstamo a buscar.
     * @return El [Prestamo] encontrado, o `null` si no existe.
     * @author Natalia González Álvarez
     */
    fun findByGuid(guid: String): Prestamo?

    /**
     * Guarda una entidad [Prestamo] en la base de datos.
     * @param prestamoRequest La entidad [Prestamo] a guardar.
     * @return La entidad [Prestamo] guardada, o `null` si la operación falla.
     * @author Natalia González Álvarez
     */
    fun save(prestamoRequest: Prestamo): Prestamo?

    /**
     * Busca una lista de préstamos por su fecha de inicio.
     * @param fecha La fecha de préstamo a buscar.
     * @return Una lista de [Prestamo] que coinciden con la fecha de préstamo.
     * @author Natalia González Álvarez
     */
    fun findByFechaPrestamo(fecha: LocalDate): List<Prestamo>

    /**
     * Busca una lista de préstamos por su fecha de devolución.
     * @param fecha La fecha de devolución a buscar.
     * @return Una lista de [Prestamo] que coinciden con la fecha de devolución.
     * @author Natalia González Álvarez
     */
    fun findByFechaDevolucion(fecha: LocalDate): List<Prestamo>

    /**
     * Busca una lista de préstamos asociados a un usuario por su GUID.
     * @param userGuid El GUID del usuario cuyas préstamos se desean buscar.
     * @return Una lista de [Prestamo] realizados por el usuario especificado.
     * @author Natalia González Álvarez
     */
    fun findByUserGuid(userGuid: String): List<Prestamo>

    /**
     * Busca una lista de préstamos por su estado.
     * @param estadoPrestamo El [EstadoPrestamo] de los préstamos a buscar.
     * @return Una lista de [Prestamo] que coinciden con el estado, o una lista vacía si no hay ninguno.
     * @author Natalia González Álvarez
     */
    fun findPrestamoByEstadoPrestamo(estadoPrestamo: EstadoPrestamo): List<Prestamo>

    /**
     * Busca una lista de préstamos asociados a un usuario por su ID.
     * @param userId El ID del usuario cuyos préstamos se desean buscar.
     * @return Una lista de [Prestamo] realizados por el usuario especificado. Puede contener `null` si un préstamo no tiene un usuario asociado válido.
     * @author Natalia González Álvarez
     */
    fun findPrestamosByUserId(userId: Long): List<Prestamo?>
}