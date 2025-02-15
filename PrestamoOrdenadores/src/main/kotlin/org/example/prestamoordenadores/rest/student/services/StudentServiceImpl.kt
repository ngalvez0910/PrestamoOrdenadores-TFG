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
import java.time.LocalDateTime

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
        var student = repository.findByGuid(guid)

        return if (student == null) {
            Err(StudentError.StudentNotFound("Student with GUID $guid not found"))
        } else {
            Ok(mapper.toStudentResponse(student))
        }
    }

    override fun createStudent(student: StudentCreateRequest): Result<StudentResponse, StudentError> {
        logger.debug { "Creating student: $student" }
        val user = userRepository.findByUsername(student.username)
        if (user == null){
            return Err(StudentError.UserNotFound("User with username ${student.username} not found"))
        }

        if (repository.existsStudentByNameAndSurNameAndGrade(student.name, student.surname, student.grade)) {
            return Err(StudentError.StudentAlreadyExists("Student with name ${student.name} ${student.surname} and grade ${student.grade} already exists"))
        }

        logger.debug { "Creating student: $student" }
        var studentModel = mapper.toStudentFromCreate(student, user)

        repository.save(studentModel)
        return Ok(mapper.toStudentResponse(studentModel))
    }

    override fun updateStudent(guid: String, student: StudentUpdateRequest): Result<StudentResponse?, StudentError> {
        logger.debug { "Getting student by GUID: $guid" }
        var studentFound = repository.findByGuid(guid)

        if (studentFound == null){
            return Err(StudentError.StudentNotFound("Student with GUID $guid not found"))
        }

        studentFound.email = student.email
        studentFound.grade = student.grade

        logger.debug { "Updating student with GUID: $guid" }
        repository.save(mapper.toStudentFromUpdate(studentFound, student))

        return Ok(mapper.toStudentResponse(studentFound))
    }

    override fun deleteStudentByGuid(guid: String): Result<StudentResponse?, StudentError> {
        logger.debug { "Deleting student with GUID: $guid" }
        var student = repository.findByGuid(guid)
        if (student == null){
            return Err(StudentError.StudentNotFound("Student with GUID $guid not found"))
        }

        student.enabled = false
        student.updatedDate = LocalDateTime.now()
        student.user.enabled = false
        student.user.updatedDate = LocalDateTime.now()

        repository.save(student)
        return Ok(mapper.toStudentResponse(student))
    }

    override fun getByGrade(grade: String): Result<List<StudentResponse?>, StudentError> {
        logger.debug { "Getting students from grade: $grade" }
        var student = repository.findByGrade(grade)

        return if (student.isEmpty()) {
            Err(StudentError.StudentNotFound("Students from grade $grade not found"))
        } else {
            Ok(mapper.toStudentResponseList(student))
        }
    }

    override fun getByName(name: String): Result<StudentResponse?, StudentError> {
        logger.debug { "Getting student with name: $name" }
        var student = repository.findByName(name)

        return if (student == null) {
            Err(StudentError.StudentNotFound("Student with name $name not found"))
        } else {
            Ok(mapper.toStudentResponse(student))
        }
    }

    override fun getByEmail(email: String): Result<StudentResponse?, StudentError> {
        logger.debug { "Getting student with email: $email" }
        var student = repository.findByEmail(email)

        return if (student == null) {
            Err(StudentError.StudentNotFound("Student with email $email not found"))
        } else {
            Ok(mapper.toStudentResponse(student))
        }
    }
}