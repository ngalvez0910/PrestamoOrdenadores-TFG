package org.example.prestamoordenadores.config.auth

import org.example.prestamoordenadores.config.auth.jwt.JwtAuthenticationFilter
import org.example.prestamoordenadores.config.auth.jwt.JwtAuthorizationFilter
import org.example.prestamoordenadores.config.auth.jwt.JwtTokenUtils
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.services.UserService
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.userdetails.UserDetailsService

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class AuthConfig
@Autowired constructor(
    private val userService: UserService,
    private val jwtTokenUtils: JwtTokenUtils,
    private val userMapper: UserMapper
) {

    @Bean
    fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)

        authenticationManagerBuilder.userDetailsService(userService as UserDetailsService)

        return authenticationManagerBuilder.build()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManager = authManager(http)

        http
            .csrf { it.disable() }
            .exceptionHandling {
                it.authenticationEntryPoint(CustomAuthenticationEntryPoint())
                it.accessDeniedHandler(CustomAccessDeniedHandler())
            }
            .authenticationManager(authenticationManager)
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests { authz ->
                // Permiso para errores y mostrarlos
                authz.requestMatchers("/error/**").permitAll()

                // Permitimos el acceso a los endpoints de swagger
                authz.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                // Permitimos el acceso a los endpoints API
                authz.requestMatchers("/api/**").permitAll()

                // Permitir el acceso a login y registro
                authz.requestMatchers("/users/login", "/users/register").permitAll()

                // Acceso para usuarios y admins
                authz.requestMatchers("/user/me").hasAnyRole("USER", "ADMIN")

                // Permitir solo acceso a admins en la lista de usuarios
                authz.requestMatchers(HttpMethod.GET, "/user/list").hasRole("ADMIN")

                // Las otras peticiones requieren autenticación
                authz.anyRequest().authenticated()
            }

            // Agregar los filtros de JWT
            .addFilter(JwtAuthenticationFilter(jwtTokenUtils, authenticationManager))
            .addFilter(JwtAuthorizationFilter(jwtTokenUtils, userService, userMapper, authenticationManager))

        return http.build()
    }
}
