package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.CreateGroupRepository

class RemoteCreateChatDataSource : BaseDataSource(), CreateGroupRepository {
    override suspend fun createChat(chat : Chat) = getResult {
        RetrofitClient.apiInterface.createChat(chat)
    }

}