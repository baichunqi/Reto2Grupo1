package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.ChatListRepository
import com.example.reto2grupo1.utils.Resource

class RemoteChatListDataSource : BaseDataSource(), ChatListRepository {
    override suspend fun getChatList(id: Int) = getResult {
        RetrofitClient.apiInterface.getChats(id)
    }

}