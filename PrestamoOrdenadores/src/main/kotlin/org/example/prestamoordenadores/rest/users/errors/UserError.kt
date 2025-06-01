package org.example.prestamoordenadores.rest.users.errors

/**
 * Representa los distintos tipos de errores relacionados con usuarios.
 *
 * @property message Mensaje descriptivo del error.
 *
 * @author Natalia González Álvarez
 */
sealed class UserError(var message: String) {

    /**
     * Error que indica que el usuario no fue encontrado.
     *
     * @param message Mensaje descriptivo del error.
     */
    class UserNotFound(message: String) : UserError(message)

    /**
     * Error que indica un fallo en la validación de datos del usuario.
     *
     * @param message Mensaje descriptivo del error.
     */
    class UserValidationError(message: String) : UserError(message)

    /**
     * Error que indica un problema relacionado con la base de datos.
     *
     * @param message Mensaje descriptivo del error.
     */
    class DataBaseError(message: String) : UserError(message)
}
