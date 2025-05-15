package org.example.prestamoordenadores.rest.dispositivos.repositories

import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DispositivoRepository : JpaRepository<Dispositivo, Long> {
    fun findByNumeroSerie(numeroSerie: String): Dispositivo?
    fun findByEstadoDispositivo(estadoDispositivo: EstadoDispositivo): List<Dispositivo>
    fun findDispositivoByGuid(guid: String): Dispositivo?
    fun findDispositivoByIncidenciaGuid(incidenciaGuid: String): Dispositivo?
    fun findByIncidenciaIdIn(incidenciaIds: List<Long>): List<Dispositivo>
}