package org.example.prestamoordenadores.rest.incidencias.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.users.models.User
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

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    var user: User = User(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false
) {
    constructor(guid: String, asunto: String, descripcion: String, estadoIncidencia: EstadoIncidencia, user: User, createdDate: LocalDateTime, updatedDate: LocalDateTime, isDeleted: Boolean) :
            this(0, guid, asunto, descripcion, estadoIncidencia, user, createdDate, updatedDate, isDeleted)
}