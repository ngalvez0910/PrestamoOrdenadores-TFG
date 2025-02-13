package org.example.prestamoordenadores

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<PrestamoOrdenadoresApplication>().with(TestcontainersConfiguration::class).run(*args)
}
