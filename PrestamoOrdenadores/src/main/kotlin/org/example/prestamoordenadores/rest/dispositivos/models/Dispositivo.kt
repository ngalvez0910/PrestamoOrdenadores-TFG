package org.example.prestamoordenadores.rest.dispositivos.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.OneToOne

/**
 * Entidad que representa un dispositivo en el sistema de gestión de préstamos.
 *
 * Mapea a la tabla "dispositivos" en la base de datos y contiene información
 * detallada sobre el dispositivo, su estado, y su relación con una posible incidencia.
 *
 * @property id El identificador único del dispositivo en la base de datos (clave primaria autoincremental).
 * @property guid El identificador único global (GUID) del dispositivo, generado automáticamente.
 * @property numeroSerie El número de serie único del dispositivo.
 * @property componentes Una descripción de los componentes del dispositivo.
 * @property estadoDispositivo El estado actual del dispositivo, utilizando el enum [EstadoDispositivo].
 * @property incidencia La incidencia asociada a este dispositivo. Es una relación uno a uno y puede ser nula.
 * @property createdDate La fecha y hora de creación del registro del dispositivo.
 * @property updatedDate La fecha y hora de la última actualización del registro del dispositivo.
 * @property isDeleted Un indicador booleano que señala si el dispositivo ha sido marcado para eliminación lógica.
 * @author Natalia González Álvarez
 */
@Entity
@Table(name = "dispositivos")
class Dispositivo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    var numeroSerie : String = "",

    var componentes :  String = "",

    @Enumerated(EnumType.STRING)
    var estadoDispositivo : EstadoDispositivo = EstadoDispositivo.DISPONIBLE,

    @OneToOne
    @JoinColumn(name = "incidencia_id", referencedColumnName = "id", unique = true)
    var incidencia: Incidencia? = null,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false
) {
    /**
     * Constructor secundario para facilitar la creación de un dispositivo con un GUID específico
     * y fechas ya definidas, útil para operaciones de carga o pruebas.
     *
     * @param guid El identificador único global (GUID) del dispositivo.
     * @param numeroSerie El número de serie único del dispositivo.
     * @param componentes Una descripción de los componentes del dispositivo.
     * @param estadoDispositivo El estado actual del dispositivo.
     * @param incidencia La incidencia asociada a este dispositivo.
     * @param isDeleted Un indicador booleano que señala si el dispositivo ha sido marcado para eliminación lógica.
     * @param createdDate La fecha y hora de creación del registro del dispositivo.
     * @param updatedDate La fecha y hora de la última actualización del registro del dispositivo.
     * @author Natalia González Álvarez
     */
    constructor(guid: String, numeroSerie: String, componentes: String, estadoDispositivo: EstadoDispositivo, incidencia: Incidencia?, isDeleted: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, numeroSerie, componentes, estadoDispositivo, incidencia, createdDate, updatedDate, isDeleted)
}