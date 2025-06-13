package org.example.prestamoordenadores.utils.locale

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Extensión de la función para [LocalDate] que formatea la fecha a una cadena de texto
 * con el formato "dd-MM-yyyy" y la localización española (España).
 *
 * @return La fecha formateada como una cadena de texto.
 * @author Natalia González Álvarez
 */
fun LocalDate.toDefaultDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale("es", "ES"))
    return this.format(formatter)
}

/**
 * Extensión de la función para [LocalDateTime] que formatea la fecha y hora a una cadena de texto
 * con el formato "dd-MM-yyyy hh:mm" y la localización española (España).
 *
 * @return La fecha y hora formateada como una cadena de texto.
 * @author Natalia González Álvarez
 */
fun LocalDateTime.toDefaultDateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm", Locale("es", "ES"))
    return this.format(formatter)
}