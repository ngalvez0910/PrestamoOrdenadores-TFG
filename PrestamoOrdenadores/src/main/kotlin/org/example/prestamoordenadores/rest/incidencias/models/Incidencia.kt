package org.example.prestamoordenadores.rest.incidencias.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateIncidenciaGuid
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

    var asunto : String = "",

    var descripcion : String = "",

    @Enumerated(EnumType.STRING)
    var estadoIncidencia : EstadoIncidencia = EstadoIncidencia.PENDIENTE,

    var userGuid: String = "",

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now()
) {
    constructor(guid: String, asunto: String, descripcion: String, estadoIncidencia: EstadoIncidencia, userGuid: String, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, asunto, descripcion, estadoIncidencia, userGuid, createdDate, updatedDate)
}