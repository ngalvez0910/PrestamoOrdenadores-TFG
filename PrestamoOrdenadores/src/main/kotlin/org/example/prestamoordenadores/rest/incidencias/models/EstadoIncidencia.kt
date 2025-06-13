package org.example.prestamoordenadores.rest.incidencias.models

/**
 * Enumeración que define los posibles estados de una incidencia.
 *
 * Describe el ciclo de vida de una incidencia desde su creación hasta su resolución.
 *
 * @author Natalia González Álvarez
 */
enum class EstadoIncidencia {
    /** La incidencia ha sido resuelta o cerrada. */
    RESUELTO,
    /** La incidencia está pendiente de ser gestionada o resuelta. */
    PENDIENTE
}