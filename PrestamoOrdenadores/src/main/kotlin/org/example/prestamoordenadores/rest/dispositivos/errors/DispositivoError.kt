package org.example.prestamoordenadores.rest.dispositivos.errors

/**
 * Clase sellada (sealed class) que define los posibles errores relacionados con la gestión de dispositivos.
 * Cada subclase representa un tipo específico de error.
 *
 * @param message El mensaje descriptivo del error.
 * @author Natalia González Álvarez
 */
sealed class DispositivoError(var message: String) {
    /**
     * Error que indica que un dispositivo no ha sido encontrado.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class DispositivoNotFound(message: String) : DispositivoError(message)

    /**
     * Error que indica un problema de validación en los datos de un dispositivo.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class DispositivoValidationError(message: String) : DispositivoError(message)

    /**
     * Error que indica que una incidencia asociada no ha sido encontrada.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class IncidenciaNotFound(message: String) : DispositivoError(message)

    /**
     * Error que indica que un usuario asociado no ha sido encontrado.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class UserNotFound(message: String) : DispositivoError(message)

    /**
     * Error que indica un problema de autenticación al acceder a recursos de dispositivo.
     * @param message El mensaje detallado del error.
     * @author Natalia González Álvarez
     */
    class AuthenticationError(message: String) : DispositivoError(message)
}