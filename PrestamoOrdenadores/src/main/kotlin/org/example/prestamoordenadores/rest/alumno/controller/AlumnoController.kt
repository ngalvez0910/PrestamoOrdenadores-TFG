package org.example.prestamoordenadores.rest.alumno.controller

import com.github.michaelbull.result.Result
import org.example.prestamoordenadores.rest.alumno.dto.AlumnoUpdateRequest
import org.example.prestamoordenadores.rest.alumno.errors.AlumnoError
import org.example.prestamoordenadores.rest.alumno.models.Alumno
import org.example.prestamoordenadores.rest.alumno.services.AlumnoService
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
@RequestMapping("/alumnos")
class AlumnoController
@Autowired constructor(
    private val alumnoService: AlumnoService
) {
    @GetMapping
    fun getAlumnos() : Result<List<Alumno>, AlumnoError> = alumnoService.getAllAlumnos()

    @GetMapping("/{guid}")
    fun getAlumnoByGuid(@PathVariable guid: String) : Result<Alumno?, AlumnoError> = alumnoService.getAlumnoByGuid(guid)

    @GetMapping("/nombre/{nombre}")
    fun getAlumnoByNombre(@PathVariable nombre: String) : Result<Alumno?, AlumnoError> = alumnoService.getByNombre(nombre)

    @GetMapping("/curso/{curso}")
    fun getAlumnosByCurso(@PathVariable curso: String) : Result<List<Alumno?>, AlumnoError> = alumnoService.getByCurso(curso)

    @GetMapping("/email/{email}")
    fun getAlumnoByEmail(@PathVariable email: String) : Result<Alumno?, AlumnoError> = alumnoService.getByEmail(email)

    @PostMapping
    fun createAlumno(@RequestBody alumno: Alumno) : Result<Alumno?, AlumnoError> = alumnoService.createAlumno(alumno)

    @PutMapping("/{guid}")
    fun updateAlumno(@PathVariable guid: String, @RequestBody alumno: AlumnoUpdateRequest) : Result<Alumno?, AlumnoError> = alumnoService.updateAlumno(guid, alumno)

    @DeleteMapping("/{guid}")
    fun deleteAlumno(@PathVariable guid: String) : Result<Alumno?, AlumnoError> = alumnoService.deleteAlumnoByGuid(guid)
}