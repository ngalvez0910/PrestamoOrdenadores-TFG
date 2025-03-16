package org.example.prestamoordenadores.rest.sanciones.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
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

    var userGuid : String = "",

    @Enumerated(EnumType.STRING)
    var tipoSancion : TipoSancion = TipoSancion.ADVERTENCIA,

    var fechaSancion : LocalDate = LocalDate.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
){
    constructor(guid: String, userGuid: String, tipoSancion: TipoSancion, fechaSancion: LocalDate, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, userGuid, tipoSancion, fechaSancion, createdDate, updatedDate)
}