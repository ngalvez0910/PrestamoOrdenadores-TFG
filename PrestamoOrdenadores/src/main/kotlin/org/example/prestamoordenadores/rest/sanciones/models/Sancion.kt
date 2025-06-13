package org.example.prestamoordenadores.rest.sanciones.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.generators.generateSancionGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidad que representa una sanción aplicada a un usuario en el sistema.
 *
 * Mapea a la tabla "sanciones" en la base de datos y contiene información
 * detallada sobre la sanción, el usuario sancionado, el préstamo asociado,
 * su tipo, fechas y estado.
 *
 * @property id El identificador único de la sanción en la base de datos (clave primaria autoincremental).
 * @property guid El identificador único global (GUID) de la sanción, generado automáticamente.
 * @property user El usuario al que se le ha aplicado la sanción. Es una relación uno a uno.
 * @property prestamo El préstamo asociado a esta sanción. Es una relación uno a uno.
 * @property tipoSancion El tipo de sanción aplicada, utilizando el enum [TipoSancion].
 * @property fechaSancion La fecha en que se aplicó la sanción.
 * @property fechaFin La fecha de finalización de la sanción. Puede ser nula para sanciones indefinidas.
 * @property createdDate La fecha y hora de creación del registro de la sanción.
 * @property updatedDate La fecha y hora de la última actualización del registro de la sanción.
 * @property isDeleted Un indicador booleano que señala si la sanción ha sido marcada para eliminación lógica.
 * @author Natalia González Álvarez
 */
@Entity
@Table(name = "sanciones")
class Sancion (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateSancionGuid(),

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    var user : User = User(),

    @OneToOne
    @JoinColumn(name = "prestamo_id", referencedColumnName = "id", unique = true)
    var prestamo : Prestamo = Prestamo(),

    @Enumerated(EnumType.STRING)
    var tipoSancion : TipoSancion = TipoSancion.ADVERTENCIA,

    var fechaSancion : LocalDate = LocalDate.now(),

    var fechaFin : LocalDate? = LocalDate.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false
){
    /**
     * Constructor secundario para facilitar la creación de una sanción con un GUID específico
     * y fechas ya definidas, útil para operaciones de carga o pruebas.
     *
     * @param guid El identificador único global (GUID) de la sanción.
     * @param user El usuario sancionado.
     * @param prestamo El préstamo asociado a la sanción.
     * @param tipoSancion El tipo de sanción.
     * @param fechaSancion La fecha de inicio de la sanción.
     * @param fechaFin La fecha de finalización de la sanción.
     * @param createdDate La fecha de creación.
     * @param updatedDate La fecha de última actualización.
     * @param isDeleted Indica si la sanción está lógicamente eliminada (por defecto false).
     * @author Natalia González Álvarez
     */
    constructor(guid: String, user: User, prestamo: Prestamo, tipoSancion: TipoSancion, fechaSancion: LocalDate, fechaFin: LocalDate, createdDate: LocalDateTime, updatedDate: LocalDateTime, isDeleted: Boolean = false) :
            this(0, guid, user, prestamo, tipoSancion, fechaSancion, fechaFin, createdDate, updatedDate, isDeleted)

    /**
     * Comprueba si la sanción está activa en la fecha actual.
     *
     * Una sanción no está activa si ha sido eliminada lógicamente.
     * La lógica de actividad varía según el [TipoSancion]:
     * - [TipoSancion.ADVERTENCIA]: Siempre activa.
     * - [TipoSancion.BLOQUEO_TEMPORAL]: Activa si la fecha actual no es posterior a la fecha de fin.
     * - [TipoSancion.INDEFINIDO]: Siempre activa.
     *
     * @return `true` si la sanción está activa, `false` en caso contrario.
     * @author Natalia González Álvarez
     */
    fun isActiveNow(): Boolean {
        if (this.isDeleted) {
            return false
        }

        val hoy = LocalDate.now()
        return when (this.tipoSancion) {
            TipoSancion.ADVERTENCIA -> {
                true
            }
            TipoSancion.BLOQUEO_TEMPORAL -> {
                !hoy.isAfter(this.fechaFin)
            }
            TipoSancion.INDEFINIDO -> {
                true
            }
        }
    }
}