package org.example.prestamoordenadores.rest.incidencias.repositories

import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repositorio para la entidad [Incidencia].
 *
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre las incidencias en la base de datos.
 *
 * @author Natalia González Álvarez
 */
@Repository
interface IncidenciaRepository : JpaRepository<Incidencia, Long> {
    /**
     * Busca una incidencia por su identificador único global (GUID).
     * @param guid El GUID de la incidencia a buscar.
     * @return La [Incidencia] encontrada, o `null` si no existe.
     * @author Natalia González Álvarez
     */
    fun findIncidenciaByGuid(guid: String) : Incidencia?

    /**
     * Busca una lista de incidencias por su estado.
     * @param estadoIncidencia El [EstadoIncidencia] de las incidencias a buscar.
     * @return Una lista de [Incidencia] que coinciden con el estado, o una lista vacía si no hay ninguna.
     * @author Natalia González Álvarez
     */
    fun findIncidenciasByEstadoIncidencia(estadoIncidencia: EstadoIncidencia) : List<Incidencia>

    /**
     * Busca una lista de incidencias asociadas a un usuario por su GUID.
     * @param userGuid El GUID del usuario cuyas incidencias se desean buscar.
     * @return Una lista de [Incidencia] reportadas por el usuario especificado.
     * @author Natalia González Álvarez
     */
    fun findIncidenciasByUserGuid(userGuid: String) : List<Incidencia>

    /**
     * Busca una lista de incidencias asociadas a un usuario por su ID.
     * @param userId El ID del usuario cuyas incidencias se desean buscar.
     * @return Una lista de [Incidencia] reportadas por el usuario especificado. Puede contener `null` si una incidencia no tiene un usuario asociado válido.
     * @author Natalia González Álvarez
     */
    fun findIncidenciasByUserId(userId: Long) : List<Incidencia?>
}