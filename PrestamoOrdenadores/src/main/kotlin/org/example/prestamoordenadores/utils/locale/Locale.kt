package org.example.prestamoordenadores.utils.locale

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDate.toDefaultDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale("es", "ES"))
    return this.format(formatter)
}

fun LocalDateTime.toDefaultDateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm", Locale("es", "ES"))
    return this.format(formatter)
}