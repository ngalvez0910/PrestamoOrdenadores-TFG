package org.example.prestamoordenadores.rest.prestamos

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "prestamos")
class Prestamo(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    var userGuid : String = "",

    var dispositivoGuid : String = "",

    var fechaLimite : LocalDate = LocalDate.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
) {
    constructor(guid: String, userGuid: String, dispositivoGuid: String, fechaLimite: LocalDate, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
        this(0, guid, userGuid, dispositivoGuid, fechaLimite, createdDate, updatedDate)
}