package org.example.prestamoordenadores.utils.generators

import kotlin.random.Random

/**
 * Genera un GUID para sanciones con el prefijo "SANC" seguido de un número de 6 dígitos aleatorio.
 *
 * El número aleatorio se genera entre 0 y 999999, y se formatea con ceros a la izquierda
 * para asegurar que siempre tenga 6 dígitos.
 *
 * @return Una cadena de texto que representa el GUID de la sanción generada.
 * @author Natalia González Álvarez
 */
fun generateSancionGuid(): String {
    val numero = Random.nextInt(0, 999999)
    return "SANC" + String.format("%06d", numero)
}