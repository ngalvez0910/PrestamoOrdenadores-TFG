package org.example.prestamoordenadores.rest.student.controller

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.student.dto.StudentCreateRequest
import org.example.prestamoordenadores.rest.student.dto.StudentResponse
import org.example.prestamoordenadores.rest.student.dto.StudentUpdateRequest
import org.example.prestamoordenadores.rest.student.errors.StudentError
import org.example.prestamoordenadores.rest.student.models.Student
import org.example.prestamoordenadores.rest.student.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/students")
class StudentController
@Autowired constructor(
    private val studentService: StudentService
) {
    @GetMapping
    fun getStudents() : Result<List<Student>, StudentError> = studentService.getAllStudents()

    @GetMapping("/{guid}")
    fun getStudentByGuid(@PathVariable guid: String) : Result<StudentResponse?, StudentError> = studentService.getStudentByGuid(guid)

    @GetMapping("/name/{name}")
    fun getStudentByName(@PathVariable name: String) : Result<StudentResponse?, StudentError> = studentService.getByName(name)

    @GetMapping("/grade/{grade}")
    fun getStudentsByGrade(@PathVariable grade: String) : Result<List<StudentResponse?>, StudentError> = studentService.getByGrade(grade)

    @GetMapping("/email/{email}")
    fun getStudentByEmail(@PathVariable email: String) : Result<StudentResponse?, StudentError> = studentService.getByEmail(email)

    @PostMapping
    fun createStudent(@RequestBody student: StudentCreateRequest) : Result<StudentResponse?, StudentError> = studentService.createStudent(student)

    @PutMapping("/{guid}")
    fun updateStudent(@PathVariable guid: String, @RequestBody student: StudentUpdateRequest) : Result<StudentResponse?, StudentError> = studentService.updateStudent(guid, student)

    @DeleteMapping("/{guid}")
    fun deleteStudent(@PathVariable guid: String) : Result<StudentResponse?, StudentError> = studentService.deleteStudentByGuid(guid)
}