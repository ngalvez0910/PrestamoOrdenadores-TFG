package org.example.prestamoordenadores.utils.pagination

data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long
)
