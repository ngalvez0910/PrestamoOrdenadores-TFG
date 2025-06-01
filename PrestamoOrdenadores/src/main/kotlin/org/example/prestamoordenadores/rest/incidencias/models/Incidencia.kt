package org.example.prestamoordenadores.rest.incidencias.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.generators.generateIncidenciaGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

/**
 * Entidad que representa una incidencia en el sistema de gestión de préstamos de ordenadores.
 *
 * Mapea a la tabla "incidencias" en la base de datos y contiene información
 * detallada sobre la incidencia, su estado, y el usuario que la reportó.
 *
 * @property id El identificador único de la incidencia en la base de datos (clave primaria autoincremental).
 * @property guid El identificador único global (GUID) de la incidencia, generado automáticamente.
 * @property asunto El asunto o título breve de la incidencia.
 * @property descripcion Una descripción más detallada del problema o incidencia.
 * @property estadoIncidencia El estado actual de la incidencia, utilizando el enum [EstadoIncidencia].
 * @property user El usuario que ha reportado la incidencia. Es una relación uno a uno.
 * @property createdDate La fecha y hora de creación del registro de la incidencia.
 * @property updatedDate La fecha y hora de la última actualización del registro de la incidencia.
 * @property isDeleted Un indicador booleano que señala si la incidencia ha sido marcada para eliminación lógica.
 * @author Natalia González Álvarez
 */
@Entity
@Table(name = "incidencias")
class Incidencia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateIncidenciaGuid(),

    var asunto : String = "",

    var descripcion : String = "",

    @Enumerated(EnumType.STRING)
    var estadoIncidencia : EstadoIncidencia = EstadoIncidencia.PENDIENTE,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    var user: User = User(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false
) {
    /**
     * Constructor secundario para facilitar la creación de una incidencia con un GUID específico
     * y fechas ya definidas, útil para operaciones de carga o pruebas.
     *
     * @param guid El identificador único global (GUID) de la incidencia.
     * @param asunto El asunto de la incidencia.
     * @param descripcion La descripción de la incidencia.
     * @param estadoIncidencia El estado de la incidencia.
     * @param user El usuario asociado a la incidencia.
     * @param createdDate La fecha de creación.
     * @param updatedDate La fecha de última actualización.
     * @param isDeleted Indica si la incidencia está lógicamente eliminada.
     * @author Natalia González Álvarez
     */
    constructor(guid: String, asunto: String, descripcion: String, estadoIncidencia: EstadoIncidencia, user: User, createdDate: LocalDateTime, updatedDate: LocalDateTime, isDeleted: Boolean) :
            this(0, guid, asunto, descripcion, estadoIncidencia, user, createdDate, updatedDate, isDeleted)
}