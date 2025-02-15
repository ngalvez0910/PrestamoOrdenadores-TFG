package org.example.prestamoordenadores.rest.student.repositories

import org.example.prestamoordenadores.rest.student.models.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    fun findByGrade(grade: String): List<Student?>
    fun findByName(name: String): Student?
    fun findByEmail(email: String): Student?
    fun findByGuid(guid: String): Student?
    fun existsStudentByNameAndSurNameAndGrade(name: String, surname:String, grade: String): Boolean
}