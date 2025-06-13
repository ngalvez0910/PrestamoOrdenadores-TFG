package org.example.prestamoordenadores.rest.sanciones.models

/**
 * Enumeración que define los posibles tipos de sanción que se pueden aplicar a un usuario.
 *
 * @author Natalia González Álvarez
 */
enum class TipoSancion {
    /** Una advertencia formal sin restricciones temporales. */
    ADVERTENCIA,
    /** Un bloqueo temporal del usuario, con una fecha de finalización definida. */
    BLOQUEO_TEMPORAL,
    /** Un bloqueo indefinido del usuario, sin una fecha de finalización establecida. */
    INDEFINIDO
}