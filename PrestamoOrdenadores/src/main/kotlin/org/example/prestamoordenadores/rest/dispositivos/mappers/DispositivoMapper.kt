package org.example.prestamoordenadores.rest.dispositivos.mappers

import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoCreateRequest
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponse
import org.example.prestamoordenadores.rest.dispositivos.dto.DispositivoResponseAdmin
import org.example.prestamoordenadores.rest.dispositivos.models.Dispositivo
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Mapper para la conversión entre entidades [Dispositivo] y sus DTOs.
 *
 * Esta clase se encarga de transformar objetos de solicitud y entidades de base de datos
 * en objetos de respuesta adecuados para la API, y viceversa.
 *
 * @author Natalia González Álvarez
 */
@Component
class DispositivoMapper {
    /**
     * Convierte una entidad [Dispositivo] a un [DispositivoResponse] básico.
     *
     * @param dispositivo La entidad [Dispositivo] a convertir.
     * @return Un [DispositivoResponse] con los datos principales del dispositivo.
     * @author Natalia González Álvarez
     */
    fun toDispositivoResponse(dispositivo: Dispositivo): DispositivoResponse {
        return DispositivoResponse(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
        )
    }

    /**
     * Convierte un [DispositivoCreateRequest] a una entidad [Dispositivo].
     *
     * Establece las fechas de creación y actualización al momento actual.
     *
     * @param dispositivo La solicitud [DispositivoCreateRequest] a convertir.
     * @return Una entidad [Dispositivo] lista para ser persistida.
     * @author Natalia González Álvarez
     */
    fun toDispositivoFromCreate(dispositivo: DispositivoCreateRequest): Dispositivo {
        return Dispositivo(
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    /**
     * Convierte una entidad [Dispositivo] a un [DispositivoResponseAdmin].
     *
     * Incluye información adicional como el estado, la incidencia asociada y el estado de eliminación lógica,
     * adecuada para vistas administrativas.
     *
     * @param dispositivo La entidad [Dispositivo] a convertir.
     * @return Un [DispositivoResponseAdmin] con todos los detalles del dispositivo.
     * @author Natalia González Álvarez
     */
    fun toDispositivoResponseAdmin(dispositivo: Dispositivo): DispositivoResponseAdmin {
        return DispositivoResponseAdmin(
            guid = dispositivo.guid,
            numeroSerie = dispositivo.numeroSerie,
            componentes = dispositivo.componentes,
            estado = dispositivo.estadoDispositivo.toString(), // Asumiendo que EstadoDispositivo tiene un toString adecuado
            incidencia = dispositivo.incidencia,
            isDeleted = dispositivo.isDeleted
        )
    }

    /**
     * Convierte una lista de entidades [Dispositivo] a una lista de [DispositivoResponseAdmin].
     *
     * @param dispositivos La lista de entidades [Dispositivo] a convertir.
     * @return Una lista de [DispositivoResponseAdmin].
     * @author Natalia González Álvarez
     */
    fun toDispositivoResponseListAdmin(dispositivos: List<Dispositivo>): List<DispositivoResponseAdmin> {
        return dispositivos.map { toDispositivoResponseAdmin(it) }
    }
}