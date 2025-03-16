package org.example.prestamoordenadores.utils.generators

import kotlin.random.Random

fun generateSancionGuid(): String {
    val numero = Random.nextInt(0, 999999)
    return "SANC" + String.format("%06d", numero)
}