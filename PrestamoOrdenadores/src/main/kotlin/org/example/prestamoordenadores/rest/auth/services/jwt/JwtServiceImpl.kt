package org.example.prestamoordenadores.rest.auth.services.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
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

    override fun extractUserName(token: String?): String? {
        logger.info{"Extracting username from token $token"}
        return extractClaim(
            token!!
        ) { obj: DecodedJWT -> obj.subject }
    }

    override fun generateToken(userDetails: UserDetails?): String? {
        logger.info{"Generating token for user ${userDetails!!.username}"}
        return generateToken(HashMap(), userDetails!!)
    }

    override fun isTokenValid(token: String?, userDetails: UserDetails?): Boolean {
        logger.info{"Validating token $token for user ${userDetails!!.username}"}
        val userName = extractUserName(token)
        return (userName == userDetails!!.username) && !isTokenExpired(token!!)
    }

    private fun <T> extractClaim(token: String, claimsResolvers: Function<DecodedJWT, T>): T {
        logger.info{"Extracting claim from token $token"}
        val decodedJWT = JWT.require(Algorithm.RSA256(publicKey, privateKey))
            .build()
            .verify(token)
        return claimsResolvers.apply(decodedJWT)
    }

    private fun generateToken(extraClaims: Map<String, Any?>, userDetails: UserDetails): String {
        val algorithm = Algorithm.RSA256(publicKey, privateKey)
        val now = Date()
        val expirationDate = Date(now.time + (1000 * jwtExpiration!!))

        return JWT.create()
            .withHeader(createHeader())
            .withSubject(userDetails.username)
            .withIssuedAt(now)
            .withExpiresAt(expirationDate)
            .withClaim("extraClaims", extraClaims)
            .sign(algorithm)
    }

    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = extractExpiration(token)
        return expirationDate.before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(
            token
        ) { obj: DecodedJWT -> obj.expiresAt }
    }

    private fun createHeader(): Map<String, Any> {
        val header: MutableMap<String, Any> = HashMap()
        header["typ"] = "JWT"
        header["alg"] = "RS256"
        return header
    }
}