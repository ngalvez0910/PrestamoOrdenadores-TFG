package org.example.prestamoordenadores.rest.prestamos.errors

/**
 * Clase sellada (sealed class) que define los posibles errores relacionados con la gestión de préstamos.
 * Cada subclase representa un tipo específico de error.
 *
 * @param message El mensaje descriptivo del error.
 * @author Natalia González Álvarez
 */
sealed class PrestamoError(var message: String) {
    /**
     * Error que indica que un préstamo no ha sido encontrado.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class PrestamoNotFound(message: String) : PrestamoError(message)

    /**
     * Error que indica que un usuario asociado al préstamo no ha sido encontrado.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class UserNotFound(message: String) : PrestamoError(message)

    /**
     * Error que indica que un dispositivo asociado al préstamo no ha sido encontrado.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class DispositivoNotFound(message: String) : PrestamoError(message)

    /**
     * Error que indica un problema de validación en los datos de un préstamo.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class PrestamoValidationError(message: String) : PrestamoError(message)
}