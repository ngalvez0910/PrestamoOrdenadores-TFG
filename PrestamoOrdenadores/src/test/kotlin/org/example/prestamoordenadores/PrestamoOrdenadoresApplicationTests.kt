package org.example.prestamoordenadores

import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
class PrestamoOrdenadoresApplicationTests {

    @MockK
    private lateinit var javaMailSender: JavaMailSender

    @Test
    fun contextLoads() {
    }

}
