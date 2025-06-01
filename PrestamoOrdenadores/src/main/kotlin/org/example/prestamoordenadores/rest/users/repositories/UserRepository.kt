package org.example.prestamoordenadores.rest.users.repositories

import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Interfaz de repositorio para la gestión de entidades [User].
 *
 * Esta interfaz extiende [JpaRepository] para proporcionar operaciones CRUD estándar
 * para objetos [User]. También define métodos de consulta personalizados para recuperar usuarios
 * basados en varios criterios como GUID, curso, nombre, correo electrónico, tutor y rol.
 *
 * @author Natalia González Álvarez
 */
@Repository
interface UserRepository: JpaRepository<User, Long> {

    /**
     * Encuentra un [User] por su identificador único (GUID).
     *
     * @param guid El GUID del usuario a buscar.
     * @return El [User] con el GUID especificado, o `null` si no se encuentra.
     */
    fun findByGuid(guid: String): User?

    /**
     * Encuentra una lista de [User]s por su curso.
     *
     * @param curso El curso por el que filtrar los usuarios.
     * @return Una lista de [User]s que pertenecen al curso especificado.
     */
    fun findByCurso(curso: String): List<User?>

    /**
     * Encuentra un [User] por su nombre.
     *
     * @param nombre El nombre del usuario a buscar.
     * @return El [User] con el nombre especificado, o `null` si no se encuentra.
     */
    fun findByNombre(nombre: String): User?

    /**
     * Encuentra un [User] por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico del usuario a buscar.
     * @return El [User] con la dirección de correo electrónico especificada, o `null` si no se encuentra.
     */
    fun findByEmail(email: String): User?

    /**
     * Encuentra una lista de [User]s por su tutor.
     *
     * @param tutor El tutor por el que filtrar los usuarios.
     * @return Una lista de [User]s asociados con el tutor especificado.
     */
    fun findByTutor(tutor: String): List<User?>

    /**
     * Encuentra una lista de [User]s por su [Role].
     *
     * @param rol El [Role] por el que filtrar los usuarios.
     * @return Una lista de [User]s que tienen el rol especificado.
     */
    fun findUsersByRol(rol: Role): List<User?>

    /**
     * Verifica si existe un [User] con el nombre, apellidos y curso dados.
     *
     * @param nombre El nombre del usuario.
     * @param apellidos Los apellidos del usuario.
     * @param curso El curso del usuario.
     * @return `true` si existe un usuario con el nombre, apellidos y curso especificados, `false` en caso contrario.
     */
    fun existsUserByNombreAndApellidosAndCurso(nombre: String, apellidos:String, curso: String): Boolean
}