package org.example.prestamoordenadores.config.websockets.models

import java.io.Serializable

data class Notification<T>(
    val entity: String?,
    val type: Tipo?,
    val data: T?,
    val createdAt: String?
) : Serializable {
    enum class Tipo {
        CREATE, UPDATE, DELETE
    }
}