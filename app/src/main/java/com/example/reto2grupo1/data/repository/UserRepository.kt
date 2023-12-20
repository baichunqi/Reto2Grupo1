package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.utils.Resource

interface UserRepository {
    suspend fun changePass(passChange: PassChange): Resource<Int>
}