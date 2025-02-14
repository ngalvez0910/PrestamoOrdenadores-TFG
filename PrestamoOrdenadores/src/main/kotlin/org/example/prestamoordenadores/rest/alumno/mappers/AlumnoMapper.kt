package org.example.prestamoordenadores.rest.alumno.mappers

import org.example.prestamoordenadores.rest.alumno.dto.AlumnoCreateRequest
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumno.models.Alumno
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AlumnoMapper {
    fun toAlumnoFromUpdate(alumno: Alumno, alumnoUpdate: AlumnoUpdateRequest): Alumno {
        return Alumno(
            id = alumno.id,
            guid = alumno.guid,
            numeroEstudiante = alumno.numeroEstudiante,
            nombre = alumno.nombre,
            apellidos = alumno.apellidos,
            email = alumnoUpdate.email,
            curso = alumnoUpdate.curso,
            fotoCarnet = alumno.fotoCarnet,
            user = alumno.user,
            enabled = alumno.enabled,
            createdDate = alumno.createdDate,
            updatedDate = LocalDateTime.now()
        )
    }

    fun toAlumnoFromCreate(alumnoCreate: AlumnoCreateRequest, user: User): Alumno {
        return Alumno(
            numeroEstudiante = alumnoCreate.numeroEstudiante,
            nombre = alumnoCreate.nombre,
            apellidos = alumnoCreate.apellidos,
            email = alumnoCreate.email,
            curso = alumnoCreate.curso,
            fotoCarnet = alumnoCreate.fotoCarnet,
            user = user
        )
    }
}