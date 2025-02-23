package org.example.prestamoordenadores.rest.alumnos.controller

import com.github.michaelbull.result.mapBoth
import org.example.prestamoordenadores.rest.users.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoResponse
import org.example.prestamoordenadores.rest.alumnos.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumnos.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumnos.models.Alumno
import org.example.prestamoordenadores.rest.alumnos.services.AlumnoService
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
class AlumnoController
@Autowired constructor(
    private val alumnoService: AlumnoService
) {
    @GetMapping
    fun getStudents() : ResponseEntity<List<Alumno>> {
        return alumnoService.getAllStudents().mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { ResponseEntity.status(422).body(null) }
        )
    }

    @GetMapping("/{guid}")
    fun getStudentByGuid(@PathVariable guid: String) : ResponseEntity<AlumnoResponse?> {
        return alumnoService.getStudentByGuid(guid).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is AlumnoError.AlumnoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/name/{name}")
    fun getStudentByName(@PathVariable name: String) : ResponseEntity<AlumnoResponse?> {
        return alumnoService.getByName(name).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is AlumnoError.AlumnoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/grade/{grade}")
    fun getStudentsByGrade(@PathVariable grade: String) : ResponseEntity<List<AlumnoResponse?>> {
        return alumnoService.getByGrade(grade).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is AlumnoError.AlumnoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @GetMapping("/email/{email}")
    fun getStudentByEmail(@PathVariable email: String) : ResponseEntity<AlumnoResponse?> {
        return alumnoService.getByEmail(email).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is AlumnoError.AlumnoNotFound -> ResponseEntity.notFound().build()
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @PostMapping
    fun createStudent(@RequestBody student: UserCreateRequest) : ResponseEntity<AlumnoResponse?> {
        return alumnoService.createStudent(student).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is AlumnoError.AlumnoNotFound -> ResponseEntity.status(404).body(null)
                    is AlumnoError.AlumnoAlreadyExists -> ResponseEntity.status(409).body(null)
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @PutMapping("/{guid}")
    fun updateStudent(@PathVariable guid: String, @RequestBody student: AlumnoUpdateRequest) : ResponseEntity<AlumnoResponse?> {
        return alumnoService.updateStudent(guid, student).mapBoth(
            success = { ResponseEntity.ok(it) },
            failure = { error ->
                when (error) {
                    is AlumnoError.AlumnoNotFound -> ResponseEntity.status(404).body(null)
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }

    @DeleteMapping("/{guid}")
    fun deleteStudent(@PathVariable guid: String) : ResponseEntity<AlumnoResponse> {
        return alumnoService.deleteStudentByGuid(guid).mapBoth(
            success = { ResponseEntity.noContent().build() },
            failure = { error ->
                when (error) {
                    is AlumnoError.AlumnoNotFound -> ResponseEntity.status(404).body(null)
                    else -> ResponseEntity.status(422).body(null)
                }
            }
        )
    }
}