package org.example.prestamoordenadores.rest.prestamos.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidad que representa un préstamo de un ordenador en el sistema.
 *
 * Mapea a la tabla "prestamos" en la base de datos y contiene información
 * detallada sobre el préstamo, el usuario, el dispositivo, su estado y las fechas.
 *
 * @property id El identificador único del préstamo en la base de datos (clave primaria autoincremental).
 * @property guid El identificador único global (GUID) del préstamo, generado automáticamente.
 * @property user El usuario al que se le ha prestado el dispositivo. Es una relación uno a uno.
 * @property dispositivo El dispositivo que ha sido prestado. Es una relación uno a uno.
 * @property estadoPrestamo El estado actual del préstamo, utilizando el enum [EstadoPrestamo].
 * @property fechaPrestamo La fecha en que se realizó el préstamo.
 * @property fechaDevolucion La fecha en que se espera o se realizó la devolución del dispositivo.
 * @property createdDate La fecha y hora de creación del registro del préstamo.
 * @property updatedDate La fecha y hora de la última actualización del registro del préstamo.
 * @property isDeleted Un indicador booleano que señala si el préstamo ha sido marcado para eliminación lógica.
 * @author Natalia González Álvarez
 */
@Entity
@Table(name = "prestamos")
class Prestamo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    var user : User = User(),

    @OneToOne
    @JoinColumn(name = "dispositivo_id", referencedColumnName = "id", unique = true)
    var dispositivo : Dispositivo = Dispositivo(),

    @Enumerated(EnumType.STRING)
    var estadoPrestamo : EstadoPrestamo = EstadoPrestamo.EN_CURSO,

    var fechaPrestamo : LocalDate = LocalDate.now(),

    var fechaDevolucion : LocalDate = LocalDate.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false
) {
    /**
     * Constructor secundario para facilitar la creación de un préstamo con un GUID específico
     * y fechas ya definidas, útil para operaciones de carga o pruebas.
     *
     * @param guid El identificador único global (GUID) del préstamo.
     * @param user El usuario asociado al préstamo.
     * @param dispositivo El dispositivo asociado al préstamo.
     * @param estadoPrestamo El estado del préstamo.
     * @param fechaPrestamo La fecha de inicio del préstamo.
     * @param fechaDevolucion La fecha de devolución del préstamo.
     * @param createdDate La fecha de creación.
     * @param updatedDate La fecha de última actualización.
     * @param isDeleted Indica si el préstamo está lógicamente eliminado.
     * @author Natalia González Álvarez
     */
    constructor(guid: String, user: User, dispositivo: Dispositivo, estadoPrestamo: EstadoPrestamo, fechaPrestamo: LocalDate, fechaDevolucion: LocalDate, createdDate: LocalDateTime, updatedDate: LocalDateTime, isDeleted: Boolean) :
            this(0, guid, user, dispositivo, estadoPrestamo, fechaPrestamo, fechaDevolucion, createdDate, updatedDate, isDeleted)
}