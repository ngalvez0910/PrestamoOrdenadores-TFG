package org.example.prestamoordenadores.rest.dispositivos.repositories

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.Estado
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DispositivoRepository : JpaRepository<Dispositivo, Long> {
    fun findByNumeroSerie(numeroSerie: String): Dispositivo
    fun findByEstado(estado: Estado): List<Dispositivo>
    fun findDispositivoByGuid(guid: String): Dispositivo
}