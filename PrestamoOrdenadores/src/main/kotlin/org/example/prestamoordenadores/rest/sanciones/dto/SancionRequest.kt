package org.example.prestamoordenadores.rest.sanciones.dto

/**
 * Clase de datos (Data Class) que representa la solicitud para crear una nueva sanción.
 *
 * @property userGuid El identificador único global (GUID) del usuario a quien se le aplicará la sanción.
 * @property tipoSancion El tipo de sanción que se desea aplicar.
 * @author Natalia González Álvarez
 */
data class SancionRequest (
    val userGuid : String,
    var tipoSancion : String,
)