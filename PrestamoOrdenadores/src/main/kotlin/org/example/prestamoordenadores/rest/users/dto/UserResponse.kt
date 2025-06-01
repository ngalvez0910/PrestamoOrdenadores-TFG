package org.example.prestamoordenadores.rest.users.dto

/**
 * Representa la información pública de un usuario que se devuelve en una respuesta.
 *
 * @property numeroIdentificacion El número de identificación único del usuario.
 * @property guid Identificador global único del usuario.
 * @property email Dirección de correo electrónico del usuario.
 * @property nombre Nombre del usuario.
 * @property apellidos Apellidos del usuario.
 * @property curso Curso actual en el que está matriculado el usuario.
 * @property tutor Nombre del tutor asignado al usuario.
 * @property avatar URL o identificador del avatar del usuario.
 *
 * @author Natalia González Álvarez
 */
data class UserResponse(
    var numeroIdentificacion: String,
    var guid: String,
    var email: String,
    var nombre: String,
    var apellidos: String,
    var curso: String,
    var tutor: String,
    var avatar: String
)
