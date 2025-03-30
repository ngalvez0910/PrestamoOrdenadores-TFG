package org.example.prestamoordenadores.rest.auth.services.authentication

import org.example.prestamoordenadores.rest.auth.dto.JwtAuthResponse
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.auth.exceptions.AuthException.AuthLoginInvalid
import org.example.prestamoordenadores.rest.auth.exceptions.AuthException.UserAuthNameOrEmailExisten
import org.example.prestamoordenadores.rest.auth.exceptions.AuthException.UserDiferentePasswords
import org.example.prestamoordenadores.rest.auth.services.jwt.JwtService
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private val log = logging()

@Service
class AuthenticationServiceImpl
@Autowired constructor(
    private val authUsersRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
): AuthenticationService {
    override fun signUp(request: UserCreateRequest?): JwtAuthResponse? {
        log.debug { "Creando usuario: $request" }

        if (request?.password.contentEquals(request?.confirmPassword)) {
            val user = User(
                numeroIdentificacion = request?.numeroIdentificacion ?:"",
                nombre = request?.nombre.toString(),
                apellidos = request?.apellidos.toString(),
                curso = request?.curso,
                tutor = request?.tutor,
                fotoCarnet = request?.fotoCarnet.toString(),
                email = request?.email ?: "",
                campoPassword = passwordEncoder.encode(request?.password),
                rol = Role.ALUMNO
            )
            try {
                val userStored = authUsersRepository.save<User>(user)
                return JwtAuthResponse(token = jwtService.generateToken(userStored))
            } catch (ex: DataIntegrityViolationException) {
                throw UserAuthNameOrEmailExisten("El usuario con username " + request?.email + " ya existe")
            }
        } else {
            throw UserDiferentePasswords("Las contraseñas no coinciden")
        }
    }

    override fun signIn(request: UserLoginRequest?): JwtAuthResponse? {
        log.debug { "Autenticando usuario: $request" }

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request!!.email, request.password)
        )

        val userDetails: UserDetails? =
            authUsersRepository.findByEmail(request.email)

        val user = userDetails ?: throw AuthLoginInvalid("Usuario o contraseña incorrectos")

        val jwt = jwtService.generateToken(user)

        return JwtAuthResponse(token = jwt)
    }
}