package org.example.prestamoordenadores.rest.alumno.repositories

import org.example.prestamoordenadores.rest.alumno.models.Alumno
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlumnoRepository : JpaRepository<Alumno, Long> {
    fun findByCurso(curso: String): List<Alumno?>
    fun findByNombre(nombre: String): Alumno?
    fun findByEmail(email: String): Alumno?
}