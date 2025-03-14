package org.example.prestamoordenadores.utils.generators

import kotlin.random.Random

fun generateNumeroSerie(): String {
    val digito = Random.nextInt(0, 10)
    val letras2 = (1..2).map { ('A'..'Z').random() }.joinToString("")
    val digitos3 = (1..3).map { Random.nextInt(0, 10) }.joinToString("")
    val letras4 = (1..4).map { ('A'..'Z').random() }.joinToString("")

    return "$digito$letras2$digitos3$letras4"
}