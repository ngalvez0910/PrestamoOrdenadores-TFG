package org.example.prestamoordenadores.rest.alumnos.repositories

import org.example.prestamoordenadores.rest.alumnos.models.Alumno
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlumnoRepository : JpaRepository<Alumno, Long> {
    fun findByGrade(grade: String): List<Alumno?>
    fun findByName(name: String): Alumno?
    fun findByEmail(email: String): Alumno?
    fun findByGuid(guid: String): Alumno?
    fun existsStudentByNameAndSurNameAndGrade(name: String, surname:String, grade: String): Boolean
}