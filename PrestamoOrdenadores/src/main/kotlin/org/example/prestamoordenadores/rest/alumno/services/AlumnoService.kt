package org.example.prestamoordenadores.rest.alumno.services

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumno.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumno.models.Alumno

interface AlumnoService {
    fun getAllAlumnos(): Result<List<Alumno>, AlumnoError>
    fun getAlumnoByGuid(guid: String) : Result<Alumno?, AlumnoError>
    fun createAlumno(alumno: Alumno) : Result<Alumno, AlumnoError>
    fun updateAlumno(guid: String, alumno: AlumnoUpdateRequest) : Result<Alumno?, AlumnoError>
    fun deleteAlumnoByGuid(guid: String) : Result<Alumno?, AlumnoError>
    fun getByCurso(curso: String): Result<List<Alumno?>, AlumnoError>
    fun getByNombre(nombre: String): Result<Alumno?, AlumnoError>
    fun getByEmail(email: String): Result<Alumno?, AlumnoError>
}