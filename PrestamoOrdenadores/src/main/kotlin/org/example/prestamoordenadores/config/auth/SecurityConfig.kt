package org.example.prestamoordenadores.config.auth

import org.example.prestamoordenadores.rest.users.services.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Configuración de seguridad principal para la aplicación.
 *
 * Esta clase habilita la seguridad web, la seguridad a nivel de método y define la cadena de filtros de seguridad.
 * Configura la autenticación basada en JWT, la gestión de sesiones sin estado y la configuración de CORS.
 *
 * @property userService Servicio de detalles de usuario personalizado para la autenticación.
 * @property jwtAuthenticationFilter Filtro JWT para la autenticación basada en tokens.
 * @author Natalia González Álvarez
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig
@Autowired constructor(
    private val userService: CustomUserDetailsService,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    /**
     * Define la cadena de filtros de seguridad HTTP.
     *
     * Configura CORS, deshabilita CSRF, establece la política de creación de sesión a `STATELESS`,
     * define las reglas de autorización para diferentes rutas y añade el filtro JWT.
     *
     * @param http El objeto [HttpSecurity] a configurar.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     * @author Natalia González Álvarez
     */
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .sessionManagement { manager: SessionManagementConfigurer<HttpSecurity?> ->
                manager.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(
                        HttpMethod.OPTIONS, "/**",
                        "/auth/signin", "/auth/signup",
                        "/ws/**",
                        "/error/**"
                    ).permitAll()
                    .requestMatchers("/notificaciones/**").authenticated()
                    .anyRequest().authenticated()
            }
            .authenticationProvider(authenticationProvider()).addFilterBefore(
                jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    /**
     * Proporciona un codificador de contraseñas utilizando BCrypt.
     *
     * @return Una instancia de [BCryptPasswordEncoder].
     * @author Natalia González Álvarez
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Proporciona un proveedor de autenticación DAO.
     *
     * Configura el servicio de detalles de usuario y el codificador de contraseñas.
     *
     * @return Una instancia de [DaoAuthenticationProvider].
     * @author Natalia González Álvarez
     */
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    /**
     * Proporciona el gestor de autenticación.
     *
     * @param config La configuración de autenticación.
     * @return Una instancia de [AuthenticationManager].
     * @throws Exception Si ocurre un error al obtener el gestor de autenticación.
     * @author Natalia González Álvarez
     */
    @Bean
    @Throws(Exception::class)
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    /**
     * Configura las políticas de Cross-Origin Resource Sharing (CORS).
     *
     * Permite orígenes específicos, métodos HTTP y cabeceras, y habilita las credenciales.
     *
     * @return Una instancia de [CorsConfigurationSource].
     * @author Natalia González Álvarez
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:[*]",
            "http://loantechoficial.com",
            "http://www.loantechoficial.com",
            "https://loantechoficial.com",
            "https://www.loantechoficial.com",
            "https://loantechoficial.onrender.com",
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("Origin", "Content-Type", "Accept", "Authorization", "Upgrade", "Connection")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}