package org.example.prestamoordenadores.rest.prestamos.models

/**
 * Enumeración que define los posibles estados de un préstamo.
 *
 * Describe el ciclo de vida de un préstamo desde su inicio hasta su finalización o cancelación.
 *
 * @author Natalia González Álvarez
 */
enum class EstadoPrestamo {
    /** El plazo del préstamo ha expirado. */
    VENCIDO,
    /** El préstamo está actualmente activo. */
    EN_CURSO,
    /** El préstamo ha sido cancelado. */
    CANCELADO,
    /** El dispositivo ha sido devuelto. */
    DEVUELTO
}