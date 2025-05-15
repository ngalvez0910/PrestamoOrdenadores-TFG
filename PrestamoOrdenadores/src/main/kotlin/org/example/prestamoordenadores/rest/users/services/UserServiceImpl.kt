package org.example.prestamoordenadores.rest.users.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.dispositivos.models.EstadoDispositivo
import org.example.prestamoordenadores.rest.dispositivos.repositories.DispositivoRepository
import org.example.prestamoordenadores.rest.incidencias.repositories.IncidenciaRepository
import org.example.prestamoordenadores.rest.prestamos.repositories.PrestamoRepository
import org.example.prestamoordenadores.rest.sanciones.repositories.SancionRepository
import org.example.prestamoordenadores.rest.users.dto.*
import org.example.prestamoordenadores.rest.users.errors.UserError
import org.example.prestamoordenadores.rest.users.mappers.UserMapper
import org.example.prestamoordenadores.rest.users.models.Role
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.example.prestamoordenadores.utils.pagination.PagedResponse
import org.example.prestamoordenadores.utils.validators.validate
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
@CacheConfig(cacheNames = ["users"])
class UserServiceImpl(
    private val repository : UserRepository,
    private val mapper: UserMapper,
    private val incidenciaRepository: IncidenciaRepository,
    private val prestamoRepository: PrestamoRepository,
    private val sancionRepository: SancionRepository,
    private val dispositivoRepository: DispositivoRepository,
): UserService {
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun getAllUsers(page: Int, size: Int): Result<PagedResponse<UserResponseAdmin>, UserError> {
        logger.debug { "Obteniendo todos los usuarios" }

        val pageRequest = PageRequest.of(page, size)
        val pageUsers = repository.findAll(pageRequest)
        val usersResponses = mapper.toUserResponseListAdmin(pageUsers.content)

        val pagedResponse = PagedResponse(
            content = usersResponses,
            totalElements = pageUsers.totalElements
        )

        return Ok(pagedResponse)
    }

    @Cacheable(key = "#guid")
    override fun getUserByGuid(guid: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con GUID: $guid" }

        val user = repository.findByGuid(guid)
        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        } else {
            Ok(mapper.toUserResponse(user))
        }
    }

    @CachePut(key = "#result.guid")
    override fun updateAvatar(guid: String, user: UserAvatarUpdateRequest): Result<UserResponse?, UserError> {
        logger.debug { "Actualizando avatar del usuario con GUID: $guid" }

        val userValidado = user.validate()
        if (userValidado.isErr) {
            return Err(UserError.UserValidationError("Usuario inválido"))
        }

        var userEncontrado = repository.findByGuid(guid)
        if (userEncontrado == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }

        userEncontrado.avatar = user.avatar
        userEncontrado.updatedDate = LocalDateTime.now()
        repository.save(userEncontrado)
        return Ok(mapper.toUserResponse(userEncontrado))
    }

    @CachePut(key = "#guid")
    override fun deleteUserByGuid(guid: String): Result<UserResponseAdmin?, UserError> {
        logger.debug { "Eliminando usuario con GUID: $guid" }

        var user = repository.findByGuid(guid)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }

        user.isActivo = false
        user.isDeleted = true
        user.updatedDate = LocalDateTime.now()
        repository.save(user)

        return Ok(mapper.toUserResponseAdmin(user))
    }

    @CachePut(key = "#guid")
    override fun resetPassword(guid: String, user: UserPasswordResetRequest): Result<UserResponse?, UserError> {
        logger.debug { "Intentando cambiar contraseña para usuario con GUID: $guid" }

        try {
            val existingUser = repository.findByGuid(guid)
                ?: return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))

            val userValidado = user.validate()
            if (userValidado.isErr) {
                return Err(UserError.UserValidationError("Datos de entrada inválidos"))
            }

            existingUser.campoPassword = passwordEncoder.encode(user.newPassword)
            existingUser.updatedDate = LocalDateTime.now()
            existingUser.lastPasswordResetDate = LocalDateTime.now()

            val savedUser = repository.save(existingUser)
            return Ok(mapper.toUserResponse(savedUser))

        } catch (e: Exception) {
            logger.error { "Error al cambiar contraseña: ${e.message}" }
            return Err(UserError.UserValidationError("Error interno al cambiar la contraseña"))
        }
    }

    @Cacheable(key = "#curso")
    override fun getByCurso(curso: String): Result<List<UserResponse?>, UserError> {
        logger.debug { "Obteniendo usuarios del curso: $curso" }

        var user = repository.findByCurso(curso)
        if (user.isEmpty()) {
            return Err(UserError.UserNotFound("Usuario del curso $curso no encontrado"))
        }

        return Ok(mapper.toUserResponseList(user))
    }

    @Cacheable(key = "#nombre")
    override fun getByNombre(nombre: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con el nombre: $nombre" }

        var user = repository.findByNombre(nombre)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con nombre $nombre no encontrado"))
        }

        return Ok(mapper.toUserResponse(user))
    }

    @Cacheable(key = "#email")
    override fun getByEmail(email: String): Result<UserResponse?, UserError> {
        logger.debug { "Obteniendo usuario con email: $email" }

        var user = repository.findByEmail(email)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con email $email no encontrado"))
        }

        return Ok(mapper.toUserResponse(user))
    }

    @Cacheable(key = "#tutor")
    override fun getByTutor(tutor: String): Result<List<UserResponse?>, UserError> {
        logger.debug { "Obteniendo usuarios con tutor: $tutor" }

        var users = repository.findByTutor(tutor)
        if (users.isEmpty()) {
            return Err(UserError.UserNotFound("Usuarios con tutor $tutor no encontrados"))
        }

        return Ok(mapper.toUserResponseList(users))
    }

    @Cacheable(key = "#guid")
    override fun getUserByGuidAdmin(guid: String): Result<UserResponseAdmin?, UserError> {
        logger.debug { "Obteniendo usuario con GUID: $guid" }

        val user = repository.findByGuid(guid)
        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        } else {
            Ok(mapper.toUserResponseAdmin(user))
        }
    }

    @Cacheable(key = "#id")
    override fun getUserById(id: Long): Result<User?, UserError> {
        logger.debug { "Obteniendo usuario con ID: $id" }

        val user = repository.findById(id).orElse(null)
        return if (user == null) {
            Err(UserError.UserNotFound("Usuario con ID $id no encontrado"))
        } else {
            Ok(user)
        }
    }

    @CachePut(key = "#result.guid")
    override fun updateUser(guid: String, request: UserUpdateRequest): Result<UserResponseAdmin?, UserError> {
        val datosValidados = request.validate()
        if (datosValidados.isErr) {
            return Err(UserError.UserValidationError("Datos inválidos"))
        }

        val user = repository.findByGuid(guid)
        if (user == null) {
            return Err(UserError.UserNotFound("Usuario con GUID $guid no encontrado"))
        }

        if (request.rol != null) {
            val nuevoRol = request.rol
            val rolEnum = Role.valueOf(nuevoRol.uppercase())
            if (user.rol != rolEnum) {
                user.rol = rolEnum
            }
        }

        if (request.isActivo != null) {
            val nuevoIsActivo = request.isActivo
            if (user.isActivo != nuevoIsActivo) {
                user.isActivo = nuevoIsActivo
                user.updatedDate = LocalDateTime.now()
                val savedUser = repository.save(user)
                return Ok(mapper.toUserResponseAdmin(savedUser))
            }
        }

        return Ok(mapper.toUserResponseAdmin(user))
    }

    @CacheEvict(cacheNames = ["users"], key = "#userGuid")
    override fun derechoAlOlvido(userGuid: String): Result<Unit, UserError> {
        logger.info { "Iniciando proceso de borrado (Derecho al Olvido) para el usuario GUID: $userGuid" }

        val user = repository.findByGuid(userGuid)
            ?: return Err(UserError.UserNotFound("Usuario no encontrado con GUID: $userGuid"))

        val userId = user.id

        try {
            val userSanciones = sancionRepository.findSancionsByUserId(userId)
            if (userSanciones.isNotEmpty()) {
                sancionRepository.deleteAll(userSanciones)
                logger.debug { "Eliminadas ${userSanciones.size} sanciones para el usuario ID: $userId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al eliminar sanciones para el usuario ID: $userId" }
            return Err(UserError.DataBaseError("Error inesperado al eliminar sanciones: ${e.localizedMessage}"))
        }

        try {
            val userPrestamos = prestamoRepository.findPrestamosByUserId(userId)
            if (userPrestamos.isNotEmpty()) {
                prestamoRepository.deleteAll(userPrestamos)
                logger.debug { "Eliminados ${userPrestamos.size} préstamos para el usuario ID: $userId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al eliminar préstamos para el usuario ID: $userId" }
            return Err(UserError.DataBaseError("Error inesperado al eliminar prestamos: ${e.localizedMessage}"))
        }

        try {
            val userIncidencias = incidenciaRepository.findIncidenciasByUserId(userId)
            if (userIncidencias.isNotEmpty()) {
                val userIncidenciaIds = userIncidencias.mapNotNull { it?.id }
                if (userIncidenciaIds.isNotEmpty()) {
                    val dispositivosToUpdate = dispositivoRepository.findByIncidenciaIdIn(userIncidenciaIds)
                    if (dispositivosToUpdate.isNotEmpty()) {
                        dispositivosToUpdate.forEach { dispositivo ->
                            dispositivo.incidencia = null
                            if (dispositivo.estadoDispositivo == EstadoDispositivo.NO_DISPONIBLE) {
                                dispositivo.estadoDispositivo = EstadoDispositivo.DISPONIBLE
                            }
                        }
                        dispositivoRepository.saveAll(dispositivosToUpdate)
                        logger.debug { "Desvinculados ${dispositivosToUpdate.size} dispositivos de incidencias del usuario ID: $userId" }
                    }
                }
                incidenciaRepository.deleteAll(userIncidencias)
                logger.debug { "Eliminadas ${userIncidencias.size} incidencias para el usuario ID: $userId" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al procesar incidencias y dispositivos para el usuario ID: $userId" }
            return Err(UserError.DataBaseError("Error inesperado al eliminar incidencias: ${e.localizedMessage}"))
        }

        try {
            repository.delete(user)
            logger.info { "Usuario ID: $userId (GUID: ${user.guid}) eliminado físicamente." }
        } catch (e: Exception) {
            logger.error(e) { "Error al eliminar el usuario ID: $userId" }
            return Err(UserError.DataBaseError("Error inesperado al eliminar usuarios: ${e.localizedMessage}"))
        }

        logger.info { "Proceso de borrado (Derecho al Olvido) completado para el usuario GUID: $userGuid" }
        return Ok(Unit)
    }
}