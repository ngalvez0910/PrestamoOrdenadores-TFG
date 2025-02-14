package org.example.prestamoordenadores.rest.student.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.student.dto.StudentCreateRequest
import org.example.prestamoordenadores.rest.student.dto.StudentResponse
import org.example.prestamoordenadores.rest.student.dto.StudentUpdateRequest
import org.example.prestamoordenadores.rest.student.errors.StudentError
import org.example.prestamoordenadores.rest.student.models.Student
import org.springframework.stereotype.Service

@Service
interface StudentService {
    fun getAllStudents(): Result<List<Student>, StudentError>
    fun getStudentByGuid(guid: String) : Result<StudentResponse?, StudentError>
    fun createStudent(student: StudentCreateRequest) : Result<StudentResponse, StudentError>
    fun updateStudent(guid: String, student: StudentUpdateRequest) : Result<StudentResponse?, StudentError>
    fun deleteStudentByGuid(guid: String) : Result<StudentResponse?, StudentError>
    fun getByGrade(grade: String): Result<List<StudentResponse?>, StudentError>
    fun getByName(name: String): Result<StudentResponse?, StudentError>
    fun getByEmail(email: String): Result<StudentResponse?, StudentError>
}