package org.example.prestamoordenadores.rest.sanciones.repositories

import org.example.prestamoordenadores.rest.sanciones.models.Sancion
import org.example.prestamoordenadores.rest.sanciones.models.TipoSancion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SancionRepository : JpaRepository<Sancion, Long> {
    fun findByGuid(guid: String): Sancion?
    fun findSancionByFechaSancion(fecha: LocalDate): List<Sancion>
    fun findSancionByTipoSancion(tipo: TipoSancion): List<Sancion>
    fun findByUserGuid(userGuid: String): List<Sancion>
}