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

@Entity
@Table(name = "usuarios")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var guid : String = generateGuid(),

    var email: String = "",

    var campoPassword: String = "",

    @Enumerated(EnumType.STRING)
    var rol: Role = Role.ALUMNO,

    var numeroIdentificacion : String = "",

    var nombre: String = "",

    var apellidos: String = "",

    var curso: String? = "",

    var tutor: String?= "",

    var fotoCarnet: String= "",

    var avatar: String = "",

    var isActivo: Boolean = false,

    var lastLoginDate: LocalDateTime = LocalDateTime.now(),

    var lastPasswordResetDate: LocalDateTime = LocalDateTime.now(),

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now(),
): UserDetails{
    constructor(guid: String, email: String, password: String, rol: Role, numeroIdentificacion: String, nombre: String, apellidos: String, curso: String, tutor: String, fotoCarnet: String, avatar: String, isActivo: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime, lastLoginDate: LocalDateTime, lastPasswordResetDate: LocalDateTime) :
            this(0, guid, email, password, rol, numeroIdentificacion, nombre, apellidos, curso, tutor, fotoCarnet, avatar, isActivo, createdDate, updatedDate, lastLoginDate, lastPasswordResetDate)

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${rol.name}"))
    }

    @JsonIgnore
    override fun getPassword(): String = campoPassword

    @JsonIgnore
    override fun getUsername(): String = email

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = true

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true
}