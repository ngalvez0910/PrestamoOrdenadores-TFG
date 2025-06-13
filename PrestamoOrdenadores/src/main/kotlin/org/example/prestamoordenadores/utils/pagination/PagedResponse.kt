package org.example.prestamoordenadores.utils.pagination

/**
 * Clase de datos que representa una respuesta paginada de una colección de elementos.
 *
 * Esta clase genérica se utiliza para encapsular una lista de contenido paginado
 * junto con el número total de elementos disponibles en la colección completa,
 * lo cual es útil para la paginación en APIs REST y otras interfaces de usuario.
 *
 * @param T El tipo de los elementos contenidos en la lista paginada.
 * @property content Una lista de elementos que representan el contenido de la página actual.
 * @property totalElements El número total de elementos disponibles en la colección completa, sin paginar.
 * @author Natalia González Álvarez
 */
data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long
)