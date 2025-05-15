package org.example.prestamoordenadores.rest.dispositivos.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.OneToOne

@Entity
@Table(name = "dispositivos")
class Dispositivo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    var numeroSerie : String = "",

    var componentes :  String = "",

    @Enumerated(EnumType.STRING)
    var estadoDispositivo : EstadoDispositivo = EstadoDispositivo.DISPONIBLE,

    @OneToOne
    @JoinColumn(name = "incidencia_id", referencedColumnName = "id", unique = true)
    var incidencia: Incidencia? = null,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false
) {
    constructor(guid: String, numeroSerie: String, componentes: String, estadoDispositivo: EstadoDispositivo, incidencia: Incidencia?, isDeleted: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, numeroSerie, componentes, estadoDispositivo, incidencia, createdDate, updatedDate, isDeleted)
}