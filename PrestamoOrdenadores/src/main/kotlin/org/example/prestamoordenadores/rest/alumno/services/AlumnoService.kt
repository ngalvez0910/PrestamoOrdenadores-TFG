package org.example.prestamoordenadores.rest.alumno.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoCreateRequest
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoResponse
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumno.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumno.models.Alumno
import org.springframework.stereotype.Service

@Service
interface AlumnoService {
    fun getAllAlumnos(): Result<List<Alumno>, AlumnoError>
    fun getAlumnoByGuid(guid: String) : Result<AlumnoResponse?, AlumnoError>
    fun createAlumno(alumno: AlumnoCreateRequest) : Result<AlumnoResponse, AlumnoError>
    fun updateAlumno(guid: String, alumno: AlumnoUpdateRequest) : Result<AlumnoResponse?, AlumnoError>
    fun deleteAlumnoByGuid(guid: String) : Result<AlumnoResponse?, AlumnoError>
    fun getByCurso(curso: String): Result<List<AlumnoResponse?>, AlumnoError>
    fun getByNombre(nombre: String): Result<AlumnoResponse?, AlumnoError>
    fun getByEmail(email: String): Result<AlumnoResponse?, AlumnoError>
}