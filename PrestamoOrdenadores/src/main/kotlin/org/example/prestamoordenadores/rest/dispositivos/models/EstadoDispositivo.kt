package org.example.prestamoordenadores.rest.dispositivos.models

/**
 * Enumeración que define los posibles estados de un dispositivo.
 *
 * Indica la disponibilidad y situación actual de un dispositivo en el sistema de préstamos.
 *
 * @author Natalia González Álvarez
 */
enum class EstadoDispositivo {
    /** El dispositivo ha sido prestado y está en uso. */
    PRESTADO,
    /** El dispositivo está disponible para ser prestado. */
    DISPONIBLE,
    /** El dispositivo no está disponible, quizás por estar en mantenimiento o dado de baja. */
    NO_DISPONIBLE
}