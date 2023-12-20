package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.repository.UserRepository
import com.example.reto2grupo1.utils.Resource

class RemoteUserDataSource : BaseDataSource(), UserRepository {

    override suspend fun changePass(passChange: PassChange): Resource<Int> = getResult {
        RetrofitClient.apiInterface.changePass(passChange)
    }
}