package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.UserRepository
import com.example.reto2grupo1.utils.Resource
import okhttp3.MultipartBody
import java.io.File

class RemoteUserDataSource : BaseDataSource(), UserRepository {

    override suspend fun changePass(passChange: PassChange): Resource<Int> = getResult {
        RetrofitClient.apiInterface.changePass(passChange)
    }

    override suspend fun uploadPhoto(photo: MultipartBody.Part) {
        RetrofitClient.apiInterface.uploadPhoto(photo)
    }
    override suspend fun getChatUsers(id:Int): Resource<List<User>> = getResult {
        RetrofitClient.apiInterface.getChatUsers(id)
    }


}