package org.example.prestamoordenadores.rest.users.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.example.prestamoordenadores.utils.generators.generateGuid
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

/**
 * Entidad que representa un usuario del sistema.
 *
 * Implementa `UserDetails` para integración con Spring Security.
 *
 * @property id Identificador único generado automáticamente.
 * @property guid Identificador global único generado para el usuario.
 * @property email Correo electrónico del usuario, utilizado como nombre de usuario.
 * @property campoPassword Contraseña cifrada del usuario.
 * @property rol Rol asignado al usuario (ADMIN, PROFESOR, ALUMNO).
 * @property numeroIdentificacion Número de identificación del usuario.
 * @property nombre Nombre del usuario.
 * @property apellidos Apellidos del usuario.
 * @property curso Curso en el que está matriculado el usuario (opcional).
 * @property tutor Nombre del tutor asignado al usuario (opcional).
 * @property avatar URL o identificador del avatar del usuario.
 * @property isActivo Indica si la cuenta está activa.
 * @property lastLoginDate Fecha del último inicio de sesión.
 * @property lastPasswordResetDate Fecha del último cambio de contraseña.
 * @property createdDate Fecha de creación del usuario.
 * @property updatedDate Fecha de la última actualización del usuario.
 * @property isDeleted Indica si el usuario ha sido eliminado lógicamente.
 * @property isOlvidado Indica si el usuario ha ejercido el derecho al olvido (GDPR).
 *
 * @author Natalia González Álvarez
 */
@Entity
@Table(name = "usuarios")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var guid: String = generateGuid(),

    var email: String = "",

    var campoPassword: String = "",

    @Enumerated(EnumType.STRING)
    var rol: Role = Role.ALUMNO,

    var numeroIdentificacion: String = "",

    var nombre: String = "",

    var apellidos: String = "",

    var curso: String? = "",

    var tutor: String? = "",

    var avatar: String = "",

    var isActivo: Boolean = false,

    var lastLoginDate: LocalDateTime = LocalDateTime.now(),

    var lastPasswordResetDate: LocalDateTime = LocalDateTime.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false,

    var isOlvidado: Boolean = false
) : UserDetails {

    /**
     * Constructor secundario con todos los parámetros, útil para tests o instanciación manual.
     */
    constructor(
        guid: String,
        email: String,
        password: String,
        rol: Role,
        numeroIdentificacion: String,
        nombre: String,
        apellidos: String,
        curso: String,
        tutor: String,
        avatar: String,
        isActivo: Boolean,
        createdDate: LocalDateTime,
        updatedDate: LocalDateTime,
        lastLoginDate: LocalDateTime,
        lastPasswordResetDate: LocalDateTime,
        isDeleted: Boolean,
        isOlvidado: Boolean
    ) : this(
        0,
        guid,
        email,
        password,
        rol,
        numeroIdentificacion,
        nombre,
        apellidos,
        curso,
        tutor,
        avatar,
        isActivo,
        lastLoginDate,
        lastPasswordResetDate,
        createdDate,
        updatedDate,
        isDeleted,
        isOlvidado
    )

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${rol.name}"))
    }

    override fun getUsername(): String = email

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = true

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @JsonIgnore
    override fun isEnabled(): Boolean = isActivo

    override fun getPassword(): String = campoPassword
}
