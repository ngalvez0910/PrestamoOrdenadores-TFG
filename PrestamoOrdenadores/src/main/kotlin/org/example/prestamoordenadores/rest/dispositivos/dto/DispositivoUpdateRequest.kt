package org.example.prestamoordenadores.rest.dispositivos.dto

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Clase de datos (Data Class) que representa la solicitud para actualizar parcialmente un dispositivo.
 *
 * Utiliza `@JsonInclude(JsonInclude.Include.NON_NULL)` para asegurar que solo los campos no nulos
 * se incluyan en la serialización JSON, permitiendo actualizaciones parciales (PATCH).
 *
 * @property componentes Una descripción actualizada de los componentes del dispositivo (opcional).
 * @property estadoDispositivo El nuevo estado del dispositivo (ej. "disponible", "prestado", "en_reparacion") (opcional).
 * @property incidenciaGuid El GUID de una incidencia asociada al dispositivo (opcional, para vincular/desvincular).
 * @property isDeleted Indica si el dispositivo debe ser marcado para eliminación lógica (opcional).
 * @author Natalia González Álvarez
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DispositivoUpdateRequest (
    var componentes: String? = null,
    var estadoDispositivo: String? = null,
    var incidenciaGuid: String? = null,
    var isDeleted: Boolean? = null
)