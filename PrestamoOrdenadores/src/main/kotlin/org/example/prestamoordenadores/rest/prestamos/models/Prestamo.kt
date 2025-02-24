package org.example.prestamoordenadores.rest.prestamos.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.jetbrains.annotations.NotNull
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

    @NotNull("User Guid no puede ser null")
    var userGuid : String = "",

    @NotNull("Dispositivo Guid no puede ser null")
    var dispositivoGuid : String = "",

    @NotNull("Estado no puede ser null")
    var estado : Estado = Estado.EN_CURSO,

    @NotNull("Fecha Prestamo no puede ser null")
    var fechaPrestamo : LocalDate = LocalDate.now(),

    @NotNull("Fecha Devolucion no puede ser null")
    var fechaDevolucion : LocalDate = LocalDate.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
) {
    constructor(guid: String, userGuid: String, dispositivoGuid: String, estado: Estado, fechaPrestamo: LocalDate, fechaDevolucion: LocalDate, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
        this(0, guid, userGuid, dispositivoGuid, estado, fechaPrestamo, fechaDevolucion, createdDate, updatedDate)
}