package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.utils.Resource

interface CommonAuthRepository {

    suspend fun myInfo() : Resource<User>

}