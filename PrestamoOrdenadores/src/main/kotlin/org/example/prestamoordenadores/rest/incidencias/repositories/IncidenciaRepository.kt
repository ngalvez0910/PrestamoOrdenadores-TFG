package org.example.prestamoordenadores.rest.incidencias.repositories

import org.example.prestamoordenadores.rest.incidencias.models.EstadoIncidencia
import org.example.prestamoordenadores.rest.incidencias.models.Incidencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IncidenciaRepository : JpaRepository<Incidencia, Long> {
    fun findIncidenciaByGuid(guid: String) : Incidencia?
    fun findIncidenciasByEstadoIncidencia(estadoIncidencia: EstadoIncidencia) : List<Incidencia>
    fun findIncidenciasByUserGuid(userGuid: String) : List<Incidencia>
}