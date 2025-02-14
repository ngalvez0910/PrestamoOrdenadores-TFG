package org.example.prestamoordenadores.rest.alumno.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoCreateRequest
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoResponse
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumno.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumno.mappers.AlumnoMapper
import org.example.prestamoordenadores.rest.alumno.models.Alumno
import org.example.prestamoordenadores.rest.alumno.repositories.AlumnoRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service

private val logger = logging()

@Service
class AlumnoServiceImpl(
    private val repository : AlumnoRepository,
    private val mapper: AlumnoMapper,
    private val userRepository : UserRepository
) : AlumnoService {
    override fun getAllAlumnos(): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Buscando todos los alumnos de la base de datos..." }
        return Ok(repository.findAll())
    }

    override fun getAlumnoByGuid(guid: String): Result<AlumnoResponse?, AlumnoError>{
        logger.debug { "Buscando alumno con guid $guid de la base de datos..." }
        var alumno = repository.findByGuid(guid)

        return if (alumno == null) {
            Err(AlumnoError.AlumnoNotFound("No se ha encontrado el alumno con guid: $guid"))
        } else {
            Ok(mapper.toAlumnoResponse(alumno))
        }
    }

    override fun createAlumno(alumno: AlumnoCreateRequest): Result<AlumnoResponse, AlumnoError> {
        logger.debug { "Buscando usuario en la base de datos..." }
        val user = userRepository.findByUsername(alumno.username)
        if (user == null){
            return Err(AlumnoError.UserNotFound("No se ha encontrado el usuario con username: ${alumno.username}"))
        }

        var alumnoModel = mapper.toAlumnoFromCreate(alumno, user)

        logger.debug { "Creando alumno en la base de datos..." }
        repository.save(alumnoModel)
        return Ok(mapper.toAlumnoResponse(alumnoModel))
    }

    override fun updateAlumno(guid: String, alumno: AlumnoUpdateRequest): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Buscando alumno con guid $guid de la base de datos..." }
        var alumnoEncontrado = repository.findByGuid(guid)

        if (alumnoEncontrado == null){
            return Err(AlumnoError.AlumnoNotFound("No se ha encontrado el alumno con guid: $guid"))
        }

        alumnoEncontrado.email = alumno.email
        alumnoEncontrado.curso = alumno.curso

        logger.debug { "Actualizando alumno con guid $guid de la base de datos..." }
        repository.save(mapper.toAlumnoFromUpdate(alumnoEncontrado, alumno))

        return Ok(mapper.toAlumnoResponse(alumnoEncontrado))
    }

    override fun deleteAlumnoByGuid(guid: String): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Eliminando alumno con guid $guid de la base de datos..." }
        var alumno = repository.deleteByGuid(guid)

        return if (alumno == null) {
            Err(AlumnoError.AlumnoNotFound("No se ha encontrado el alumno con guid: $guid"))
        } else {
            Ok(mapper.toAlumnoResponse(alumno))
        }
    }

    override fun getByCurso(curso: String): Result<List<AlumnoResponse?>, AlumnoError> {
        logger.debug { "Buscando alumnos del curso $curso en la base de datos..." }
        var alumno = repository.findByCurso(curso)

        return if (alumno.isEmpty()) {
            Err(AlumnoError.AlumnoNotFound("No hay alumnos en el curso: $curso"))
        } else {
            Ok(mapper.toAlumnoResponseList(alumno))
        }
    }

    override fun getByNombre(nombre: String): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Buscando alumno con nombre $nombre en la base de datos..." }
        var alumno = repository.findByNombre(nombre)

        return if (alumno == null) {
            Err(AlumnoError.AlumnoNotFound("No se ha encontrado el alumno con nombre: $nombre"))
        } else {
            Ok(mapper.toAlumnoResponse(alumno))
        }
    }

    override fun getByEmail(email: String): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Buscando alumno con email $email en la base de datos..." }
        var alumno = repository.findByEmail(email)

        return if (alumno == null) {
            Err(AlumnoError.AlumnoNotFound("No se ha encontrado el alumno con email: $email"))
        } else {
            Ok(mapper.toAlumnoResponse(alumno))
        }
    }
}