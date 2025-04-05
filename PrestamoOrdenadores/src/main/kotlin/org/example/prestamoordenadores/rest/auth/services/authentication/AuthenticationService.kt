package org.example.prestamoordenadores.rest.auth.services.authentication

import org.example.prestamoordenadores.rest.auth.dto.JwtAuthResponse
import org.example.prestamoordenadores.rest.auth.dto.UserCreateRequest
import org.example.prestamoordenadores.rest.auth.dto.UserLoginRequest

interface AuthenticationService {
    fun signUp(request: UserCreateRequest?): JwtAuthResponse?
    fun signIn(request: UserLoginRequest?): JwtAuthResponse?
}