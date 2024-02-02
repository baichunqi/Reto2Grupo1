package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.CreateGroupRepository
import com.example.reto2grupo1.utils.Resource

class RemoteCreateChatDataSource : BaseDataSource(), CreateGroupRepository {
    override suspend fun createChat(chat : Chat): Resource<Void> = getResult {
        RetrofitClient.apiInterface.createChat(chat)
    }

}