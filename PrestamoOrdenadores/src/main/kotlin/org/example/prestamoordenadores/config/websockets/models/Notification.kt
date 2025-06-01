package org.example.prestamoordenadores.config.websockets.models

import java.io.Serializable

/**
 * Clase de datos que representa una notificación genérica para WebSockets.
 * Implementa [Serializable] para permitir su transmisión.
 *
 * @param T El tipo de datos que contiene la notificación.
 * @property entity El nombre de la entidad a la que se refiere la notificación (opcional).
 * @property type El tipo de operación que representa la notificación (CREATE, UPDATE, DELETE) (opcional).
 * @property data Los datos asociados a la notificación, del tipo [T] (opcional).
 * @property createdAt La fecha y hora de creación de la notificación en formato String (opcional).
 * @author Natalia González Álvarez
 */
data class Notification<T>(
    val entity: String?,
    val type: Tipo?,
    val data: T?,
    val createdAt: String?
) : Serializable {
    /**
     * Enumeración que define los tipos de operaciones para una notificación.
     * @author Natalia González Álvarez
     */
    enum class Tipo {
        CREATE, UPDATE, DELETE
    }
}