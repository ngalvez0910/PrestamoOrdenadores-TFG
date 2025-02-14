package org.example.prestamoordenadores.rest.student.mappers

import org.example.prestamoordenadores.rest.student.dto.StudentCreateRequest
import org.example.prestamoordenadores.rest.student.dto.StudentResponse
import org.example.prestamoordenadores.rest.student.dto.StudentUpdateRequest
import org.example.prestamoordenadores.rest.student.models.Student
import org.example.prestamoordenadores.rest.users.models.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StudentMapper {
    fun toStudentFromUpdate(student: Student, studentUpdate: StudentUpdateRequest): Student {
        return Student(
            id = student.id,
            guid = student.guid,
            studentNumber = student.studentNumber,
            name = student.name,
            surName = student.surName,
            email = studentUpdate.email,
            grade = studentUpdate.grade,
            image = student.image,
            user = student.user,
            enabled = student.enabled,
            createdDate = student.createdDate,
            updatedDate = LocalDateTime.now()
        )
    }

    fun toStudentFromCreate(studentCreate: StudentCreateRequest, user: User): Student {
        return Student(
            studentNumber = studentCreate.studentNumber,
            name = studentCreate.name,
            surName = studentCreate.surname,
            email = studentCreate.email,
            grade = studentCreate.grade,
            image = studentCreate.image,
            user = user
        )
    }

    fun toStudentResponse(student: Student): StudentResponse{
        return StudentResponse(
            guid = student.guid,
            name = student.name,
            surname = student.surName,
            email = student.email,
            grade = student.grade,
            createdDate = student.createdDate.toString(),
            updatedDate = student.updatedDate.toString()
        )
    }

    fun toStudentResponseList(students: List<Student?>): List<StudentResponse> {
        return students.map { toStudentResponse(it!!) }
    }
}