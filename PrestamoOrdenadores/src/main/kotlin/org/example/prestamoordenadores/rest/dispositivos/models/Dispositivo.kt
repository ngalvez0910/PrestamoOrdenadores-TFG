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
import org.jetbrains.annotations.NotNull
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

    @NotNull("Numero de serie no puede ser null")
    var numeroSerie : String = generateNumeroSerie(),

    @NotNull("Componentes no puede ser null")
    var componentes :  String = "",

    @Enumerated(EnumType.STRING)
    @NotNull("Estado no puede ser null")
    var estado : Estado = Estado.NUEVO,

    var incidenciaGuid : String = "",

    @NotNull("Stock no puede ser null")
    var stock: Int = 0,

    @NotNull("Activo no puede ser null")
    var isActivo: Boolean = true,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
) {
    constructor(guid: String, numeroSerie: String, componentes: String, estado: Estado, incidenciaGuid: String, stock: Int, isActivo: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, numeroSerie, componentes, estado, incidenciaGuid, stock, isActivo, createdDate, updatedDate)
}