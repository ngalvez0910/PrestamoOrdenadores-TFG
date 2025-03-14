package org.example.prestamoordenadores.rest.incidencias.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "incidencias")
class Incidencia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    @Enumerated(EnumType.STRING)
    @NotNull("Estado no puede estar vacío")
    var estado : Estado = Estado.RESUELTO,

    var userGuid: String = "",

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now()
) {
    constructor(guid: String, estado: Estado, userGuid: String, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, estado, userGuid, createdDate, updatedDate)
}