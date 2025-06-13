package org.example.prestamoordenadores.utils.generators

import java.security.SecureRandom

/**
 * Genera un GUID (Globally Unique Identifier) aleatorio de 11 caracteres.
 *
 * Este GUID está compuesto por una combinación de letras mayúsculas, minúsculas y dígitos.
 * Utiliza [SecureRandom] para asegurar una generación de números aleatorios criptográficamente fuerte.
 *
 * @return Una cadena de texto que representa el GUID generado.
 * @author Natalia González Álvarez
 */
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