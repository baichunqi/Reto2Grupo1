package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.DbUser
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.utils.Resource

interface CommonUserRepository {
    suspend fun getUsers():Resource<List<User>>

    suspend fun showInfo() : Resource<User>
    suspend fun getLoggedEmail():String
    suspend fun addUser(user:User):Long

}