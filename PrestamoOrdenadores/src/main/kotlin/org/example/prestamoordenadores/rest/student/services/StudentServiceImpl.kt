package org.example.prestamoordenadores.rest.student.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.student.dto.StudentCreateRequest
import org.example.prestamoordenadores.rest.student.dto.StudentResponse
import org.example.prestamoordenadores.rest.student.dto.StudentUpdateRequest
import org.example.prestamoordenadores.rest.student.errors.StudentError
import org.example.prestamoordenadores.rest.student.mappers.StudentMapper
import org.example.prestamoordenadores.rest.student.models.Student
import org.example.prestamoordenadores.rest.student.repositories.StudentRepository
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.lighthousegames.logging.logging
import org.springframework.stereotype.Service

private val logger = logging()

@Service
class StudentServiceImpl(
    private val repository : StudentRepository,
    private val mapper: StudentMapper,
    private val userRepository : UserRepository
) : StudentService {
    override fun getAllStudents(): Result<List<Student>, StudentError> {
        logger.debug { "Getting all students" }
        return Ok(repository.findAll())
    }

    override fun getStudentByGuid(guid: String): Result<StudentResponse?, StudentError>{
        logger.debug { "Getting student by GUID: $guid" }
        var alumno = repository.findByGuid(guid)

        return if (alumno == null) {
            Err(StudentError.StudentNotFound("Student with GUID $guid not found"))
        } else {
            Ok(mapper.toStudentResponse(alumno))
        }
    }

    override fun createStudent(student: StudentCreateRequest): Result<StudentResponse, StudentError> {
        logger.debug { "Creating student: $student" }
        val user = userRepository.findByUsername(student.username)
        if (user == null){
            return Err(StudentError.UserNotFound("User with username ${student.username} not found"))
        }

        var alumnoModel = mapper.toStudentFromCreate(student, user)

        repository.save(alumnoModel)
        return Ok(mapper.toStudentResponse(alumnoModel))
    }

    override fun updateStudent(guid: String, student: StudentUpdateRequest): Result<StudentResponse?, StudentError> {
        logger.debug { "Getting student by GUID: $guid" }
        var alumnoEncontrado = repository.findByGuid(guid)

        if (alumnoEncontrado == null){
            return Err(StudentError.StudentNotFound("Student with GUID $guid not found"))
        }

        alumnoEncontrado.email = student.email
        alumnoEncontrado.grade = student.grade

        logger.debug { "Updating student with GUID: $guid" }
        repository.save(mapper.toStudentFromUpdate(alumnoEncontrado, student))

        return Ok(mapper.toStudentResponse(alumnoEncontrado))
    }

    override fun deleteStudentByGuid(guid: String): Result<StudentResponse?, StudentError> {
        logger.debug { "Deleting student with GUID: $guid" }
        var alumno = repository.deleteByGuid(guid)

        return if (alumno == null) {
            Err(StudentError.StudentNotFound("Student with GUID $guid not found"))
        } else {
            Ok(mapper.toStudentResponse(alumno))
        }
    }

    override fun getByGrade(grade: String): Result<List<StudentResponse?>, StudentError> {
        logger.debug { "Getting students from grade: $grade" }
        var alumno = repository.findByGrade(grade)

        return if (alumno.isEmpty()) {
            Err(StudentError.StudentNotFound("Students from grade $grade not found"))
        } else {
            Ok(mapper.toStudentResponseList(alumno))
        }
    }

    override fun getByName(name: String): Result<StudentResponse?, StudentError> {
        logger.debug { "Getting student with name: $name" }
        var alumno = repository.findByName(name)

        return if (alumno == null) {
            Err(StudentError.StudentNotFound("Student with name $name not found"))
        } else {
            Ok(mapper.toStudentResponse(alumno))
        }
    }

    override fun getByEmail(email: String): Result<StudentResponse?, StudentError> {
        logger.debug { "Getting student with email: $email" }
        var alumno = repository.findByEmail(email)

        return if (alumno == null) {
            Err(StudentError.StudentNotFound("Student with email $email not found"))
        } else {
            Ok(mapper.toStudentResponse(alumno))
        }
    }
}