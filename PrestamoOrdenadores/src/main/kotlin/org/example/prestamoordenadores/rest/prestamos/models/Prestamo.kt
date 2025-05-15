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
    constructor(guid: String, user: User, dispositivo: Dispositivo, estadoPrestamo: EstadoPrestamo, fechaPrestamo: LocalDate, fechaDevolucion: LocalDate, createdDate: LocalDateTime, updatedDate: LocalDateTime, isDeleted: Boolean) :
            this(0, guid, user, dispositivo, estadoPrestamo, fechaPrestamo, fechaDevolucion, createdDate, updatedDate, isDeleted)
}