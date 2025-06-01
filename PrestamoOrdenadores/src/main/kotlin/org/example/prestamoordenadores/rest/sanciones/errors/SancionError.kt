package org.example.prestamoordenadores.rest.sanciones.errors

/**
 * Clase sellada (sealed class) que define los posibles errores relacionados con la gestión de sanciones.
 * Cada subclase representa un tipo específico de error.
 *
 * @param message El mensaje descriptivo del error.
 * @author Natalia González Álvarez
 */
sealed class SancionError(var message: String) {
    /**
     * Error que indica que una sanción no ha sido encontrada.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class SancionNotFound(message: String) : SancionError(message)

    /**
     * Error que indica que un usuario asociado a la sanción no ha sido encontrado.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class UserNotFound(message: String) : SancionError(message)

    /**
     * Error que indica un problema de validación en los datos de una sanción.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class SancionValidationError(message: String) : SancionError(message)
}