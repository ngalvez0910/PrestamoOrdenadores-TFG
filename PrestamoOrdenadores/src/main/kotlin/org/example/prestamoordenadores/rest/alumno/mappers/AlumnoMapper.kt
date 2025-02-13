package org.example.prestamoordenadores.rest.alumno.mappers

import org.example.prestamoordenadores.rest.alumno.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumno.models.Alumno

fun AlumnoUpdateRequest.toAlumno(): Alumno {
    return Alumno(
        email = this.email,
        curso = this.curso
    )
}