package org.example.prestamoordenadores.rest.dispositivos.dto

import org.example.prestamoordenadores.rest.incidencias.models.Incidencia

/**
 * Clase de datos (Data Class) que representa la respuesta detallada de un dispositivo para administradores.
 *
 * Incluye información completa sobre el dispositivo, su estado, una posible incidencia asociada
 * y su estado de eliminación lógica, lo que la hace adecuada para vistas administrativas.
 *
 * @property guid El identificador único global (GUID) del dispositivo.
 * @property numeroSerie El número de serie del dispositivo.
 * @property componentes Una descripción de los componentes del dispositivo.
 * @property estado El estado actual del dispositivo (ej. "disponible", "prestado", "en_reparacion").
 * @property incidencia La incidencia asociada al dispositivo, si la hay (puede ser nula).
 * @property isDeleted Indica si el dispositivo ha sido marcado para eliminación lógica.
 * @author Natalia González Álvarez
 */
data class DispositivoResponseAdmin (
    var guid: String,
    var numeroSerie: String,
    var componentes: String,
    var estado: String,
    var incidencia: Incidencia?,
    var isDeleted: Boolean
)