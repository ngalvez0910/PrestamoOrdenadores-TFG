package org.example.prestamoordenadores.rest.alumnos.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoResponse
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumnos.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumnos.mappers.AlumnoMapper
import org.example.prestamoordenadores.rest.alumnos.models.Alumno
import org.example.prestamoordenadores.rest.alumnos.repositories.AlumnoRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = logging()

@Service
class AlumnoServiceImpl(
    private val repository : AlumnoRepository,
    private val mapper: AlumnoMapper,
    private val userRepository : UserRepository
) : AlumnoService {
    override fun getAllStudents(): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Getting all students" }
        return Ok(repository.findAll())
    }

    override fun getStudentByGuid(guid: String): Result<AlumnoResponse?, AlumnoError>{
        logger.debug { "Getting student by GUID: $guid" }
        var student = repository.findByGuid(guid)

        return if (student == null) {
            Err(AlumnoError.AlumnoNotFound("Student with GUID $guid not found"))
        } else {
            Ok(mapper.toStudentResponse(student))
        }
    }

    override fun createStudent(student: UserCreateRequest): Result<AlumnoResponse, AlumnoError> {
        logger.debug { "Creating student: $student" }
        val user = userRepository.findByUsername(student.username)
        if (user == null){
            return Err(AlumnoError.UserNotFound("User with username ${student.username} not found"))
        }

        if (repository.existsStudentByNameAndSurNameAndGrade(student.nombre, student.apellidos, student.curso)) {
            return Err(AlumnoError.AlumnoAlreadyExists("Student with name ${student.nombre} ${student.apellidos} and grade ${student.curso} already exists"))
        }

        logger.debug { "Creating student: $student" }
        var studentModel = mapper.toStudentFromCreate(student, user)

        repository.save(studentModel)
        return Ok(mapper.toStudentResponse(studentModel))
    }

    override fun updateStudent(guid: String, student: AlumnoUpdateRequest): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Getting student by GUID: $guid" }
        var studentFound = repository.findByGuid(guid)

        if (studentFound == null){
            return Err(AlumnoError.AlumnoNotFound("Student with GUID $guid not found"))
        }

        studentFound.email = student.email
        studentFound.curso = student.grade

        logger.debug { "Updating student with GUID: $guid" }
        repository.save(mapper.toStudentFromUpdate(studentFound, student))

        return Ok(mapper.toStudentResponse(studentFound))
    }

    override fun deleteStudentByGuid(guid: String): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Deleting student with GUID: $guid" }
        var student = repository.findByGuid(guid)
        if (student == null){
            return Err(AlumnoError.AlumnoNotFound("Student with GUID $guid not found"))
        }

        student.isActivo = false
        student.updatedDate = LocalDateTime.now()
        student.user.isActivo = false
        student.user.updatedDate = LocalDateTime.now()

        repository.save(student)
        return Ok(mapper.toStudentResponse(student))
    }

    override fun getByGrade(grade: String): Result<List<AlumnoResponse?>, AlumnoError> {
        logger.debug { "Getting students from grade: $grade" }
        var student = repository.findByGrade(grade)

        return if (student.isEmpty()) {
            Err(AlumnoError.AlumnoNotFound("Students from grade $grade not found"))
        } else {
            Ok(mapper.toStudentResponseList(student))
        }
    }

    override fun getByName(name: String): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Getting student with name: $name" }
        var student = repository.findByName(name)

        return if (student == null) {
            Err(AlumnoError.AlumnoNotFound("Student with name $name not found"))
        } else {
            Ok(mapper.toStudentResponse(student))
        }
    }

    override fun getByEmail(email: String): Result<AlumnoResponse?, AlumnoError> {
        logger.debug { "Getting student with email: $email" }
        var student = repository.findByEmail(email)

        return if (student == null) {
            Err(AlumnoError.AlumnoNotFound("Student with email $email not found"))
        } else {
            Ok(mapper.toStudentResponse(student))
        }
    }
}