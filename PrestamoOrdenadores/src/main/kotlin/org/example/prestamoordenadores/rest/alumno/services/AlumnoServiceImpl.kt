package org.example.prestamoordenadores.rest.alumno.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoCreateRequest
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumno.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumno.mappers.AlumnoMapper
import org.example.prestamoordenadores.rest.alumno.models.Alumno
import org.example.prestamoordenadores.rest.alumno.repositories.AlumnoRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service

@Service
class AlumnoServiceImpl(
    private var repository : AlumnoRepository,
    private var mapper: AlumnoMapper
) : AlumnoService {
    override fun getAllAlumnos(): Result<List<Alumno>, AlumnoError> {
        logging("Buscando todos los alumnos de la base de datos...")
        return Ok(repository.findAll())
    }

    override fun getAlumnoByGuid(guid: String): Result<Alumno?, AlumnoError>{
        logging("Buscando alumno con guid $guid de la base de datos...")
        return Ok(repository.findByGuid(guid))
    }

    override fun createAlumno(alumno: AlumnoCreateRequest): Result<Alumno, AlumnoError> {
        logging("Creando alumno en la base de datos...")
        return Ok(repository.save(mapper.toAlumnoFromCreate(alumno)))
    }

    override fun updateAlumno(guid: String, alumno: AlumnoUpdateRequest): Result<Alumno?, AlumnoError> {
        logging("Buscando alumno con guid $guid de la base de datos...")
        var alumnoEncontrado = repository.findByGuid(guid)

        if (alumnoEncontrado == null){
            return Err(AlumnoError.AlumnoNotFound("No se ha encontrado el alumno con guid: $guid"))
        }

        alumnoEncontrado.email = alumno.email
        alumnoEncontrado.curso = alumno.curso

        logging("Actualizando alumno con guid $guid de la base de datos...")
        return Ok(repository.save(mapper.toAlumnoFromUpdate(alumnoEncontrado, alumno)))
    }

    override fun deleteAlumnoByGuid(guid: String): Result<Alumno?, AlumnoError> {
        logging("Eliminando alumno con guid $guid de la base de datos...")
        return Ok(repository.deleteByGuid(guid))
    }

    override fun getByCurso(curso: String): Result<List<Alumno?>, AlumnoError> {
        logging("Buscando alumnos del curso $curso en la base de datos...")
        return Ok(repository.findByCurso(curso))
    }

    override fun getByNombre(nombre: String): Result<Alumno?, AlumnoError> {
        logging("Buscando alumno con nombre $nombre en la base de datos...")
        return Ok(repository.findByNombre(nombre))
    }

    override fun getByEmail(email: String): Result<Alumno?, AlumnoError> {
        logging("Buscando alumno con email $email en la base de datos...")
        return Ok(repository.findByEmail(email))
    }
}