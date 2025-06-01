package org.example.prestamoordenadores.utils.generators

import kotlin.random.Random

/**
 * Genera un GUID para incidencias con el prefijo "INC" seguido de un número de 6 dígitos aleatorio.
 *
 * El número aleatorio se genera entre 0 y 999999, y se formatea con ceros a la izquierda
 * para asegurar que siempre tenga 6 dígitos.
 *
 * @return Una cadena de texto que representa el GUID de la incidencia generada.
 * @author Natalia González Álvarez
 */
fun generateIncidenciaGuid(): String {
    val numero = Random.nextInt(0, 999999)
    return "INC" + String.format("%06d", numero)
}