package org.example.prestamoordenadores.rest.student.controller

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.student.dto.StudentCreateRequest
import org.example.prestamoordenadores.rest.student.dto.StudentResponse
import org.example.prestamoordenadores.rest.student.dto.StudentUpdateRequest
import org.example.prestamoordenadores.rest.student.errors.StudentError
import org.example.prestamoordenadores.rest.student.models.Student
import org.example.prestamoordenadores.rest.student.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
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
    fun getStudents() : ResponseEntity<List<Student>> {
        return studentService.getAllStudents().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @GetMapping("/{guid}")
    fun getStudentByGuid(@PathVariable guid: String) : ResponseEntity<StudentResponse?> {
        return studentService.getStudentByGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is StudentError.StudentNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/name/{name}")
    fun getStudentByName(@PathVariable name: String) : ResponseEntity<StudentResponse?> {
        return studentService.getByName(name).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is StudentError.StudentNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/grade/{grade}")
    fun getStudentsByGrade(@PathVariable grade: String) : ResponseEntity<List<StudentResponse?>> {
        return studentService.getByGrade(grade).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is StudentError.StudentNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/email/{email}")
    fun getStudentByEmail(@PathVariable email: String) : ResponseEntity<StudentResponse?> {
        return studentService.getByEmail(email).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is StudentError.StudentNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @PostMapping
    fun createStudent(@RequestBody student: StudentCreateRequest) : ResponseEntity<StudentResponse?> {
        return studentService.createStudent(student).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is StudentError.StudentNotFound -> ResponseEntity.status(404).body(null)
                    is StudentError.StudentAlreadyExists -> ResponseEntity.status(409).body(null)
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @PutMapping("/{guid}")
    fun updateStudent(@PathVariable guid: String, @RequestBody student: StudentUpdateRequest) : ResponseEntity<StudentResponse?> {
        return studentService.updateStudent(guid, student).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is StudentError.StudentNotFound -> ResponseEntity.status(404).body(null)
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deleteStudent(@PathVariable guid: String) : ResponseEntity<StudentResponse> {
        return studentService.deleteStudentByGuid(guid).mapBoth(
            success = { ResponseEntity.noContent().build() },
            failure = { error ->
                when (error) {
                    is StudentError.StudentNotFound -> ResponseEntity.status(404).body(null)
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }
}