package org.example.prestamoordenadores.rest.alumnos.mappers

import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoCreateRequest
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoResponse
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumnos.models.Alumno
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AlumnoMapper {
    fun toStudentFromUpdate(alumno: Alumno, studentUpdate: AlumnoUpdateRequest): Alumno {
        return Alumno(
            id = alumno.id,
            guid = alumno.guid,
            numeroEstudiante = alumno.numeroEstudiante,
            nombre = alumno.nombre,
            apellidos = alumno.apellidos,
            email = studentUpdate.email,
            curso = studentUpdate.grade,
            fotoCarnet = alumno.fotoCarnet,
            user = alumno.user,
            isActivo = alumno.isActivo,
            createdDate = alumno.createdDate,
            updatedDate = LocalDateTime.now()
        )
    }

    fun toStudentFromCreate(studentCreate: AlumnoCreateRequest, user: User): Alumno {
        return Alumno(
            numeroEstudiante = studentCreate.studentNumber,
            nombre = studentCreate.name,
            apellidos = studentCreate.surname,
            email = studentCreate.email,
            curso = studentCreate.grade,
            fotoCarnet = studentCreate.image,
            user = user
        )
    }

    fun toStudentResponse(alumno: Alumno): AlumnoResponse{
        return AlumnoResponse(
            guid = alumno.guid,
            name = alumno.nombre,
            surname = alumno.apellidos,
            email = alumno.email,
            grade = alumno.curso,
            createdDate = alumno.createdDate.toString(),
            updatedDate = alumno.updatedDate.toString()
        )
    }

    fun toStudentResponseList(alumnos: List<Alumno?>): List<AlumnoResponse> {
        return alumnos.map { toStudentResponse(it!!) }
    }
}