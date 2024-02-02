package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.utils.Resource
import okhttp3.MultipartBody
import java.io.File

interface UserRepository {
    suspend fun changePass(passChange: PassChange): Resource<Int>
    suspend fun uploadPhoto(photo: MultipartBody.Part)

    suspend fun getChatUsers(id : Int): Resource<List<User>>
}