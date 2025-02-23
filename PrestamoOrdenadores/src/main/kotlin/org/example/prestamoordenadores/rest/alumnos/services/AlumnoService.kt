package org.example.prestamoordenadores.rest.alumnos.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoCreateRequest
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoResponse
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumnos.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumnos.models.Alumno
import org.springframework.stereotype.Service

@Service
interface AlumnoService {
    fun getAllStudents(): Result<List<Alumno>, AlumnoError>
    fun getStudentByGuid(guid: String) : Result<AlumnoResponse?, AlumnoError>
    fun createStudent(student: AlumnoCreateRequest) : Result<AlumnoResponse, AlumnoError>
    fun updateStudent(guid: String, student: AlumnoUpdateRequest) : Result<AlumnoResponse?, AlumnoError>
    fun deleteStudentByGuid(guid: String) : Result<AlumnoResponse?, AlumnoError>
    fun getByGrade(grade: String): Result<List<AlumnoResponse?>, AlumnoError>
    fun getByName(name: String): Result<AlumnoResponse?, AlumnoError>
    fun getByEmail(email: String): Result<AlumnoResponse?, AlumnoError>
}