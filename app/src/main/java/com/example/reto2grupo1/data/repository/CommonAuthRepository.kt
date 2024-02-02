package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.AuthenticationRequest

interface CommonAuthRepository {
    suspend fun login(email: String, pass:String):Boolean

    suspend fun changeToLogged(email:String)

    suspend fun changeToUnLogged()

    suspend fun saveUser(email: String, pass:String)
}