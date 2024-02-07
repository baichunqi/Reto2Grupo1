package com.example.reto2grupo1.data.repository.remote

import android.util.Log
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.Rol
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.UserRepository
import com.example.reto2grupo1.utils.Resource
import okhttp3.MultipartBody

class RemoteUserDataSource : BaseDataSource(), UserRepository {

    override suspend fun changePass(passChange: PassChange): Resource<Int> = getResult {
        RetrofitClient.apiInterface.changePass(passChange)
    }
    override suspend fun uploadPhoto(photo: MultipartBody.Part) {
        RetrofitClient.apiInterface.uploadPhoto(photo)
    }
    override suspend fun getChatUsers(id:Int): Resource<List<User>> = getResult {
        RetrofitClient.apiInterface.getUsersChat(id)
    }
//    override suspend fun changePassword(authenticationRequest: AuthenticationRequest): Resource<AuthenticationRequest> = getResult {
//        RetrofitClient.apiInterface.changePassword(authenticationRequest)
//    }

    override suspend fun getUserRol(): Resource<Rol> = getResult {
        RetrofitClient.apiInterface.getUserRol()
    }

    override suspend fun dissAssingUser(chatId: Int, userId: Int) : Resource<Void> = getResult {
        Log.d("RemoteRepositori", "$chatId  $userId" )
        RetrofitClient.apiInterface.dissasingUser(chatId , userId )
    }


}