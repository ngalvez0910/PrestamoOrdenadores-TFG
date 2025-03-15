package org.example.prestamoordenadores.rest.users.dto

import org.jetbrains.annotations.NotNull

data class UserAvatarUpdateRequest (
    @NotNull("Avatar no puede ser null")
    var avatar : String
)