package org.example.prestamoordenadores.utils.generators

import kotlin.random.Random

fun generateIncidenciaGuid(): String {
    val numero = Random.nextInt(0, 999999)
    return "INC" + String.format("%06d", numero)
}