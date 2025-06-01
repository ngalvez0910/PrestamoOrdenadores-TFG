package org.example.prestamoordenadores.rest.dispositivos.dto

/**
 * Clase de datos (Data Class) que representa la solicitud para crear un nuevo dispositivo.
 * Contiene la información esencial necesaria para registrar un dispositivo en el sistema.
 *
 * @property numeroSerie El número de serie único del dispositivo.
 * @property componentes Una descripción de los componentes del dispositivo.
 * @author Natalia González Álvarez
 */
data class DispositivoCreateRequest(
    val numeroSerie: String,
    val componentes: String
)