package org.example.prestamoordenadores.rest.dispositivos.dto

/**
 * Clase de datos (Data Class) que representa la respuesta detallada de un dispositivo.
 * Contiene la información completa de un dispositivo, incluyendo su identificador único (GUID),
 * número de serie y componentes.
 *
 * @property guid El identificador único global (GUID) del dispositivo.
 * @property numeroSerie El número de serie del dispositivo.
 * @property componentes Una descripción de los componentes del dispositivo.
 * @author Natalia González Álvarez
 */
data class DispositivoResponse (
    var guid: String,
    var numeroSerie: String,
    var componentes: String
)