package org.example.prestamoordenadores.rest.users.services

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.example.prestamoordenadores.rest.auth.exceptions.UserNotFoundException
import org.example.prestamoordenadores.rest.users.models.User
import org.example.prestamoordenadores.rest.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CustomUserDetailsServiceTest {

    @MockK
    lateinit var repository: UserRepository

    lateinit var userDetailsService : CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        userDetailsService = CustomUserDetailsService(repository)
    }

    @Test
    fun loadUserByUsername() {
        val email = "user@example.com"
        val mockUser = mockk<User>()
        every { repository.findByEmail(email) } returns mockUser

        val result = userDetailsService.loadUserByUsername(email)

        assertEquals(mockUser, result)
        verify(exactly = 1) { repository.findByEmail(email) }
    }

    @Test
    fun `loadUserByUsername throws UserNotFoundException when user no existe`() {
        val email = "notfound@example.com"
        every { repository.findByEmail(email) } returns null

        val exception = assertThrows<UserNotFoundException> {
            userDetailsService.loadUserByUsername(email)
        }

        assertEquals(email, exception.message)
        verify(exactly = 1) { repository.findByEmail(email) }
    }
}