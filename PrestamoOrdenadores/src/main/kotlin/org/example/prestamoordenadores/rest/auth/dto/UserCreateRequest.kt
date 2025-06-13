package org.example.prestamoordenadores.rest.auth.dto

import org.jetbrains.annotations.NotNull

/**
 * Clase de datos (Data Class) que representa la solicitud para crear un nuevo usuario.
 * Contiene los campos necesarios para el registro de un usuario, con anotaciones de validación
 * para asegurar que ciertos campos no sean nulos.
 *
 * @property numeroIdentificacion El número de identificación del usuario. No puede ser nulo.
 * @property nombre El nombre del usuario. No puede ser nulo.
 * @property apellidos Los apellidos del usuario. No puede ser nulo.
 * @property email La dirección de correo electrónico del usuario. No puede ser nulo.
 * @property curso El curso al que pertenece el usuario (opcional).
 * @property tutor El nombre del tutor del usuario (opcional).
 * @property password La contraseña del usuario. No puede estar vacía.
 * @property confirmPassword La confirmación de la contraseña del usuario. No puede estar vacía.
 * @author Natalia González Álvarez
 */
data class UserCreateRequest (
    @NotNull("Numero identificacion no puede ser null")
    var numeroIdentificacion: String?,
    @NotNull("Nombre no puede ser null")
    var nombre: String?,
    @NotNull("Apellidos no puede ser null")
    var apellidos: String?,
    @NotNull("Email no puede ser null")
    var email: String?,
    var curso: String?,
    var tutor: String?,
    @NotNull("Password no puede estar vacío")
    var password : String?,
    @NotNull("Confirm password no puede estar vacío")
    var confirmPassword : String?
)