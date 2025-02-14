package org.example.prestamoordenadores.rest.student.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.prestamoordenadores.rest.users.models.User
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "students")
class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,
    var guid : String = "",

    @NotNull("Student Number cannot be null")
    var studentNumber : String = "",

    @NotNull("Name cannot be null")
    var name: String = "",

    @NotNull("Surname cannot be null")
    var surName: String = "",

    @NotNull("Email cannot be null")
    var email: String = "",

    @NotNull("Grade cannot be null")
    var grade: String = "",

    @NotNull("Image cannot be null")
    var image: String = "",

    @OneToOne
    @JoinColumn(name = "user_guid")
    var user: User = User(),
    var enabled: Boolean = true,

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedDate: LocalDateTime = LocalDateTime.now()
){
    constructor(guid: String, studentNumber: String, name: String, surName: String, email: String, grade: String, image: String, user: User, enabled: Boolean, createdDate: LocalDateTime, updatedDate: LocalDateTime) :
            this(0, guid, studentNumber, name, surName, email, grade, image, user, enabled, createdDate, updatedDate
            )
}