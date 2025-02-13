package org.example.prestamoordenadores

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PrestamoOrdenadoresApplication

fun main(args: Array<String>) {
    runApplication<PrestamoOrdenadoresApplication>(*args)
    println("✅ PrestamoOrdenadores iniciado")
}