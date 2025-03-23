package org.example.prestamoordenadores.rest.users.dto

import org.jetbrains.annotations.NotNull

data class UserRoleUpdateRequest (
    @NotNull("Rol no puede ser null")
    var rol : String
)