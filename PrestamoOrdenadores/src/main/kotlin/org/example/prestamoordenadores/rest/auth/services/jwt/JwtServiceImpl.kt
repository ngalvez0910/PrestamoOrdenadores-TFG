package org.example.prestamoordenadores.rest.auth.services.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.example.prestamoordenadores.rest.users.models.User
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cglib.core.internal.Function
import org.springframework.core.io.Resource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

private val logger = logging()

/**
 * Implementación del servicio JWT para la creación, validación y extracción de información de tokens.
 *
 * Utiliza claves RSA (pública y privada) cargadas desde recursos de classpath para firmar y verificar tokens.
 *
 * @property privateKeyResource Recurso que contiene la clave privada PKCS8 PEM para la firma de tokens.
 * @property publicKeyResource Recurso que contiene la clave pública PEM para la verificación de tokens.
 * @author Natalia González Álvarez
 */
@Service
class JwtServiceImpl(
    @Value("classpath:private_key_pkcs8.pem") privateKeyResource: Resource,
    @Value("classpath:public_key.pem") publicKeyResource: Resource
) :
    JwtService {
    @Value("\${jwt.expiration:86400}")
    private val jwtExpiration: Long? = null

    private val privateKey: RSAPrivateKey = loadPrivateKey(privateKeyResource)
    private val publicKey: RSAPublicKey = loadPublicKey(publicKeyResource)

    /**
     * Carga la clave privada RSA desde un recurso de classpath.
     * El archivo PEM debe estar en formato PKCS8 y se eliminan las cabeceras y los saltos de línea.
     *
     * @param resource El [Resource] que apunta al archivo de la clave privada.
     * @return La [RSAPrivateKey] cargada.
     * @author Natalia González Álvarez
     */
    private fun loadPrivateKey(resource: Resource): RSAPrivateKey {
        val privateKeyPEM = StreamUtils.copyToString(resource.inputStream, StandardCharsets.UTF_8)
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val encoded = Base64.getDecoder().decode(privateKeyPEM)
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(encoded)
        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }

    /**
     * Carga la clave pública RSA desde un recurso de classpath.
     * El archivo PEM se espera que esté en formato X.509 y se eliminan las cabeceras y los saltos de línea.
     *
     * @param resource El [Resource] que apunta al archivo de la clave pública.
     * @return La [RSAPublicKey] cargada.
     * @author Natalia González Álvarez
     */
    private fun loadPublicKey(resource: Resource): RSAPublicKey {
        val publicKeyPEM = StreamUtils.copyToString(resource.inputStream, StandardCharsets.UTF_8)
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val encoded = Base64.getDecoder().decode(publicKeyPEM)
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = X509EncodedKeySpec(encoded)
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }

    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     *
     * @param token El token JWT del que se extraerá el nombre de usuario.
     * @return El nombre de usuario.
     * @author Natalia González Álvarez
     */
    override fun extractUserName(token: String?): String? {
        logger.info{"Extracting username from token $token"}
        return extractClaim(
            token!!
        ) { obj: DecodedJWT -> obj.subject }
    }

    /**
     * Genera un token JWT para un usuario.
     *
     * @param userDetails Los detalles del usuario para el que se generará el token.
     * @return El token JWT generado.
     * @author Natalia González Álvarez
     */
    override fun generateToken(userDetails: UserDetails?): String? {
        logger.info{"Generating token for user ${userDetails!!.username}"}
        return generateToken(HashMap(), userDetails!!)
    }

    /**
     * Valida un token JWT.
     *
     * Comprueba si el nombre de usuario extraído del token coincide con el de los detalles del usuario
     * y si el token no ha expirado.
     *
     * @param token El token JWT a validar.
     * @param userDetails Los detalles del usuario con los que se comparará el token.
     * @return `true` si el token es válido para el usuario, `false` en caso contrario.
     * @author Natalia González Álvarez
     */
    override fun isTokenValid(token: String?, userDetails: UserDetails?): Boolean {
        logger.info{"Validating token $token for user ${userDetails!!.username}"}
        val userName = extractUserName(token)
        return (userName == userDetails!!.username) && !isTokenExpired(token!!)
    }

    /**
     * Extrae un "claim" específico de un token JWT.
     *
     * Utiliza una función de resolución para obtener el valor del claim deseado.
     *
     * @param token El token JWT del que se extraerá el claim.
     * @param claimsResolvers La función que define cómo extraer el claim de un [DecodedJWT].
     * @return El valor del claim extraído.
     * @author Natalia González Álvarez
     */
    private fun <T> extractClaim(token: String, claimsResolvers: Function<DecodedJWT, T>): T {
        logger.info{"Extracting claim from token $token"}
        val decodedJWT = JWT.require(Algorithm.RSA256(publicKey, privateKey))
            .build()
            .verify(token)
        return claimsResolvers.apply(decodedJWT)
    }

    /**
     * Genera un token JWT con los claims proporcionados y los detalles del usuario.
     *
     * Incluye el sujeto (nombre de usuario), la fecha de emisión, la fecha de expiración,
     * el rol del usuario y cualquier claim extra.
     *
     * @param extraClaims Un mapa de claims adicionales a incluir en el token.
     * @param userDetails Los detalles del usuario para el que se generará el token.
     * @return El token JWT generado como una cadena de texto.
     * @author Natalia González Álvarez
     */
    private fun generateToken(extraClaims: Map<String, Any?>, userDetails: UserDetails): String {
        val algorithm = Algorithm.RSA256(publicKey, privateKey)
        val now = Date()
        val expirationDate = Date(now.time + (1000 * jwtExpiration!!))

        val user = userDetails as User

        return JWT.create()
            .withHeader(createHeader())
            .withSubject(user.username)
            .withIssuedAt(now)
            .withExpiresAt(expirationDate)
            .withClaim("rol", user.rol.name)
            .withClaim("extraClaims", extraClaims)
            .sign(algorithm)
    }

    /**
     * Verifica si un token JWT ha expirado.
     *
     * @param token El token JWT a verificar.
     * @return `true` si el token ha expirado, `false` en caso contrario.
     * @author Natalia González Álvarez
     */
    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = extractExpiration(token)
        return expirationDate.before(Date())
    }

    /**
     * Extrae la fecha de expiración de un token JWT.
     *
     * @param token El token JWT del que se extraerá la fecha de expiración.
     * @return La [Date] de expiración del token.
     * @author Natalia González Álvarez
     */
    private fun extractExpiration(token: String): Date {
        return extractClaim(
            token
        ) { obj: DecodedJWT -> obj.expiresAt }
    }

    /**
     * Crea el encabezado estándar para un token JWT.
     *
     * @return Un mapa que representa el encabezado del token.
     * @author Natalia González Álvarez
     */
    private fun createHeader(): Map<String, Any> {
        val header: MutableMap<String, Any> = HashMap()
        header["typ"] = "JWT"
        header["alg"] = "RS256"
        return header
    }
}