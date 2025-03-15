package org.example.prestamoordenadores.rest.incidencias.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.example.prestamoordenadores.utils.generators.generateIncidenciaGuid
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
    var guid : String = generateIncidenciaGuid(),

    @Enumerated(EnumType.STRING)
    @NotNull("Estado no puede estar vacío")
    var estadoIncidencia : EstadoIncidencia = EstadoIncidencia.PENDIENTE,

    var userGuid: String = "",

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now()
) {
    constructor(guid: String, estadoIncidencia: EstadoIncidencia, userGuid: String, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, estadoIncidencia, userGuid, createdDate, updatedDate)
}