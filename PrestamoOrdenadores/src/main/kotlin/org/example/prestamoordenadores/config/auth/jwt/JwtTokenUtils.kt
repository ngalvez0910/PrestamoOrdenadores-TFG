package org.example.prestamoordenadores.config.auth.jwt

import org.example.prestamoordenadores.rest.users.models.User
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.example.prestamoordenadores.config.auth.exceptions.TokenInvalidoException

private val logger = logging()

@Component
class JwtTokenUtils {
    @Value("\${jwt.secret}")
    private val jwtSecreto: String? =
        null

    @Value("\${jwt.token-expiration:3600}")
    private val jwtDuracionTokenEnSegundos = 0

    fun generateToken(user: User): String {
        logger.info { "Generando token para el user: ${user.email}" }

        // val user: Usuario = authentication.principal

        val tokenExpirationDate = Date(System.currentTimeMillis() + jwtDuracionTokenEnSegundos * 1000)

        return JWT.create()
            .withSubject(user.guid)
            .withHeader(mapOf("typ" to TOKEN_TYPE))
            .withIssuedAt(Date())
            .withExpiresAt(tokenExpirationDate)
            .withClaim("email", user.email)
            .withClaim("nombre", user.nombre)
            .withClaim("rol", user.rol.toString())
            //.withClaim("authorities", user.authorities.map { it.authority }.toList())
            .sign(Algorithm.HMAC512(jwtSecreto))
    }

    fun getUserIdFromJwt(token: String?): String {
        logger.info { "Obteniendo el ID del usuario: $token" }
        return validateToken(token!!)!!.subject
    }

    fun validateToken(authToken: String): DecodedJWT? {
        logger.info { "Validando el token: ${authToken}" }

        try {
            return JWT.require(Algorithm.HMAC512(jwtSecreto)).build().verify(authToken)
        } catch (e: Exception) {
            throw TokenInvalidoException("Token no válido o expirado")
        }
    }

    private fun getClaimsFromJwt(token: String) =
        validateToken(token)?.claims

    fun getUsernameFromJwt(token: String): String {
        logger.info { "Obteniendo el nombre de usuario del token: ${token}" }

        val claims = getClaimsFromJwt(token)
        return claims!!["username"]!!.asString()
    }

    fun getRolesFromJwt(token: String): String {
        logger.info { "Obteniendo los roles del token: ${token}" }

        val claims = getClaimsFromJwt(token)
        return claims!!["roles"]!!.asString()
    }

    fun isTokenValid(token: String): Boolean {
        logger.info { "Comprobando si el token es válido: ${token}" }

        val claims = getClaimsFromJwt(token)!!
        val expirationDate = claims["exp"]!!.asDate()
        val now = Date(System.currentTimeMillis())
        return now.before(expirationDate)
    }

    companion object {
        const val TOKEN_HEADER = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
        const val TOKEN_TYPE = "JWT"
    }
}