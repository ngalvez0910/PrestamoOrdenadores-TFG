package org.example.prestamoordenadores.rest.auth.services.authentication

import org.example.prestamoordenadores.rest.auth.dto.JwtAuthResponse
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest
import org.example.prestamoordenadores.rest.auth.exceptions.AuthException.AuthLoginInvalid
import org.example.prestamoordenadores.rest.auth.exceptions.AuthException.UserAuthEmailExist
import org.example.prestamoordenadores.rest.auth.exceptions.AuthException.UserDiferentePasswords
import org.example.prestamoordenadores.rest.auth.services.jwt.JwtService
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl
@Autowired constructor(
    private val authUsersRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) : AuthenticationService {

    override fun signUp(request: UserCreateRequest?): JwtAuthResponse {
        if (request!!.password == request.confirmPassword) {
            val rol = when {
                request.email!!.endsWith(".loantech.profesor@gmail.com") -> Role.PROFESOR
                request.email!!.endsWith(".loantech.admin@gmail.com") -> Role.ADMIN
                request.email!!.endsWith(".loantech@gmail.com") -> Role.ALUMNO
                else -> Role.ALUMNO
            }

            val user = User(
                numeroIdentificacion = request.numeroIdentificacion!!,
                nombre = request.nombre!!,
                apellidos = request.apellidos!!,
                email = request.email!!,
                curso = request.curso,
                tutor = request.tutor,
                campoPassword = passwordEncoder.encode(request.password),
                rol = rol
            )
            try {
                val userStored = authUsersRepository.save(user)
                return JwtAuthResponse(token = jwtService.generateToken(userStored))
            } catch (ex: DataIntegrityViolationException) {
                throw UserAuthEmailExist("El usuario con email ${request.email} ya existe")
            }
        } else {
            throw UserDiferentePasswords("Las contraseñas no coinciden")
        }
    }

    override fun signIn(request: UserLoginRequest?): JwtAuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request!!.email, request.password)
        )

        val user = authUsersRepository.findByEmail(request.email)
        if (user == null) throw AuthLoginInvalid("Usuario o contraseña incorrectos")

        val jwt = jwtService.generateToken(user)

        return JwtAuthResponse(token = jwt)
    }
}