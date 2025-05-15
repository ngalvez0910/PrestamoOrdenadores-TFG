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
    constructor(guid: String, user: User, prestamo: Prestamo, tipoSancion: TipoSancion, fechaSancion: LocalDate, fechaFin: LocalDate, createdDate: LocalDateTime, updatedDate: LocalDateTime, isDeleted: Boolean = false) :
            this(0, guid, user, prestamo, tipoSancion, fechaSancion, fechaFin, createdDate, updatedDate, isDeleted)

    fun isActiveNow(): Boolean {
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