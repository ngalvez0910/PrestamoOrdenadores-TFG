package org.example.prestamoordenadores.rest.dispositivos.repositories

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repositorio para la entidad [Dispositivo].
 *
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre los dispositivos en la base de datos.
 *
 * @author Natalia González Álvarez
 */
@Repository
interface DispositivoRepository : JpaRepository<Dispositivo, Long> {
    /**
     * Busca un dispositivo por su número de serie.
     * @param numeroSerie El número de serie del dispositivo a buscar.
     * @return El [Dispositivo] encontrado, o `null` si no existe.
     * @author Natalia González Álvarez
     */
    fun findByNumeroSerie(numeroSerie: String): Dispositivo?

    /**
     * Busca una lista de dispositivos por su estado.
     * @param estadoDispositivo El [EstadoDispositivo] de los dispositivos a buscar.
     * @return Una lista de [Dispositivo] que coinciden con el estado, o una lista vacía si no hay ninguno.
     * @author Natalia González Álvarez
     */
    fun findByEstadoDispositivo(estadoDispositivo: EstadoDispositivo): List<Dispositivo>

    /**
     * Busca un dispositivo por su GUID (identificador único global).
     * @param guid El GUID del dispositivo a buscar.
     * @return El [Dispositivo] encontrado, o `null` si no existe.
     * @author Natalia González Álvarez
     */
    fun findDispositivoByGuid(guid: String): Dispositivo?

    /**
     * Busca un dispositivo por el GUID de su incidencia asociada.
     * @param incidenciaGuid El GUID de la incidencia asociada al dispositivo a buscar.
     * @return El [Dispositivo] que tiene la incidencia con el GUID especificado, o `null` si no existe.
     * @author Natalia González Álvarez
     */
    fun findDispositivoByIncidenciaGuid(incidenciaGuid: String): Dispositivo?

    /**
     * Busca una lista de dispositivos cuyas incidencias tienen IDs que están en la lista proporcionada.
     * @param incidenciaIds Una lista de IDs de incidencias.
     * @return Una lista de [Dispositivo] que tienen incidencias con los IDs especificados.
     * @author Natalia González Álvarez
     */
    fun findByIncidenciaIdIn(incidenciaIds: List<Long>): List<Dispositivo>
}