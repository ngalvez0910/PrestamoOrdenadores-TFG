package org.example.prestamoordenadores.rest.incidencias.errors

/**
 * Clase sellada (sealed class) que define los posibles errores relacionados con la gestión de incidencias.
 * Cada subclase representa un tipo específico de error.
 *
 * @param message El mensaje descriptivo del error.
 * @author Natalia González Álvarez
 */
sealed class IncidenciaError(var message: String) {
    /**
     * Error que indica que una incidencia no ha sido encontrada.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class IncidenciaNotFound(message: String) : IncidenciaError(message)

    /**
     * Error que indica que un usuario asociado a la incidencia no ha sido encontrado.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class UserNotFound(message: String) : IncidenciaError(message)

    /**
     * Error que indica un problema de validación en los datos de una incidencia.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class IncidenciaValidationError(message: String) : IncidenciaError(message)
}