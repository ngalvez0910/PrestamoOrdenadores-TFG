package org.example.prestamoordenadores.utils.generators

import java.security.SecureRandom

fun generateGuid(): String{
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val rng = SecureRandom()
    val id = StringBuilder(11)

    repeat(11) {
        val index = rng.nextInt(characters.length)
        id.append(characters[index])
    }

    return id.toString()
}