package org.example.prestamoordenadores.rest.prestamos.repositories

import org.example.prestamoordenadores.rest.prestamos.models.Prestamo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PrestamoRepository : JpaRepository<Prestamo, Long> {
    fun findByGuid(guid: String): Prestamo?
    fun save(prestamoRequest: Prestamo): Prestamo?
    fun findByFechaPrestamo(fecha: LocalDate): List<Prestamo?>
    fun findByFechaLimite(fecha: LocalDate): List<Prestamo?>
    fun findByUserGuid(userGuid: String): List<Prestamo?>
}