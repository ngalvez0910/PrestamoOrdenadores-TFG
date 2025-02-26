package org.example.prestamoordenadores.rest.dispositivos.repositories

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DispositivoRepository : JpaRepository<Dispositivo, Long> {
    fun findByNumeroSerie(numeroSerie: String): Dispositivo
    fun findByEstado(estado: String): List<Dispositivo>
    fun findDispositivoByGuid(guid: String): Dispositivo
}