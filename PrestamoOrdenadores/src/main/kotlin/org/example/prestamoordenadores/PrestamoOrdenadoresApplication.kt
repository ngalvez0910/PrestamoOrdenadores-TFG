package org.example.prestamoordenadores

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PrestamoOrdenadoresApplication

fun main(args: Array<String>) {
    runApplication<PrestamoOrdenadoresApplication>(*args)
    println("✅ PrestamoOrdenadores iniciado")
}