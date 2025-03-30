package org.example.prestamoordenadores.config.auth

import jakarta.servlet.Filter
import org.example.prestamoordenadores.rest.users.services.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig
@Autowired constructor(
    private var userService: CustomUserDetailsService?,
    private var jwtAuthenticationFilter: JwtAuthenticationFilter?
){
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .csrf(Customizer<CsrfConfigurer<HttpSecurity>> { obj -> obj.disable() })
            .sessionManagement(Customizer<SessionManagementConfigurer<HttpSecurity>> { manager ->
                manager.sessionCreationPolicy(
                    STATELESS
                )
            })
            .authorizeHttpRequests(Customizer { request ->
                request
                    .requestMatchers("/auth/signin", "/auth/signup").permitAll()
//                    .requestMatchers("/error/**").permitAll()
//                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
//                    .permitAll()
//                    .requestMatchers("/static/**").permitAll()
//                    .requestMatchers("/ws/**").permitAll()
//                    .requestMatchers("/" + "/**").permitAll()
                    .anyRequest().authenticated()
            })
            .authenticationProvider(authenticationProvider()).addFilterBefore(
                jwtAuthenticationFilter as Filter?, UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? {
        return config.getAuthenticationManager()
    }
}