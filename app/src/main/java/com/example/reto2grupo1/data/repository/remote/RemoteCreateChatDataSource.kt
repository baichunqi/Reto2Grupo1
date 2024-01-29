package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.repository.CreateGroupRepository

class RemoteCreateChatDataSource : BaseDataSource(), CreateGroupRepository {
    override suspend fun createChat(name : String, private : Boolean) = getResult {
        RetrofitClient.apiInterface.createChat(name, private)
    }

}