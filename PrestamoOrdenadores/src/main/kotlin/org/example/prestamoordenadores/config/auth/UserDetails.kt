package org.example.prestamoordenadores.config.auth

import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetails(
    private val user: User
) : UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(user.rol.name))
    }

    override fun getPassword(): String = user.password
    override fun getUsername(): String = user.email
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = user.isActivo
}
