package org.example.prestamoordenadores.rest.dispositivos.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.example.prestamoordenadores.utils.generators.generateNumeroSerie
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "dispositivos")
class Dispositivo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    var numeroSerie : String = generateNumeroSerie(),

    var componentes :  String = "",

    @Enumerated(EnumType.STRING)
    var estado : Estado = Estado.NUEVO,

    var incidenciaGuid : String? = "",

    var stock: Int = 0,

    var isActivo: Boolean = true,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
) {
    constructor(guid: String, numeroSerie: String, componentes: String, estado: Estado, incidenciaGuid: String, stock: Int, isActivo: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, numeroSerie, componentes, estado, incidenciaGuid, stock, isActivo, createdDate, updatedDate)
}