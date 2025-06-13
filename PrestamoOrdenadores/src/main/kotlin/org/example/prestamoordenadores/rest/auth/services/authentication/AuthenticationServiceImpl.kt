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

/**
 * Implementación del servicio de autenticación para el registro e inicio de sesión de usuarios.
 *
 * Este servicio interactúa con el repositorio de usuarios, el codificador de contraseñas,
 * el servicio JWT y el gestor de autenticación de Spring Security para manejar las operaciones
 * de autenticación.
 *
 * @property authUsersRepository Repositorio para acceder a los datos de los usuarios.
 * @property passwordEncoder Codificador de contraseñas para el hashing de contraseñas.
 * @property jwtService Servicio para la generación de tokens JWT.
 * @property authenticationManager Gestor de autenticación de Spring Security.
 * @author Natalia González Álvarez
 */
@Service
class AuthenticationServiceImpl
@Autowired constructor(
    private val authUsersRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) : AuthenticationService {

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Valida que las contraseñas coincidan y asigna un rol al usuario basándose en el sufijo de su correo electrónico.
     * Si el correo ya existe, lanza una excepción [UserAuthEmailExist].
     *
     * @param request La solicitud que contiene los datos para crear el usuario.
     * @return Un [JwtAuthResponse] con el token JWT si el registro es exitoso.
     * @throws UserDiferentePasswords Si las contraseñas proporcionadas no coinciden.
     * @throws UserAuthEmailExist Si ya existe un usuario con el correo electrónico proporcionado.
     * @author Natalia González Álvarez
     */
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

    /**
     * Inicia sesión de un usuario existente.
     *
     * Autentica al usuario utilizando el [AuthenticationManager] y, si las credenciales son válidas,
     * genera un token JWT para el usuario. Si el usuario no existe o las credenciales son incorrectas,
     * lanza una excepción [AuthLoginInvalid].
     *
     * @param request La solicitud que contiene las credenciales del usuario para iniciar sesión.
     * @return Un [JwtAuthResponse] con el token JWT si el inicio de sesión es exitoso.
     * @throws AuthLoginInvalid Si el usuario o la contraseña son incorrectos.
     * @author Natalia González Álvarez
     */
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