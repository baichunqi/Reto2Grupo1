package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.AuthenticationResponse
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.utils.Resource

interface AuthenticationRepository {
    suspend fun login(authenticationRequest: AuthenticationRequest) : Resource<AuthenticationResponse>
    suspend fun myInfo() : Resource<User>

}