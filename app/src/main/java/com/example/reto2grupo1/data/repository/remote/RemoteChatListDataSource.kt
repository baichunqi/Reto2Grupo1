package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.ChatListRepository

class RemoteChatListDataSource : BaseDataSource(), ChatListRepository {
    override suspend fun getChatList(user: User) {
        RetrofitClient.apiInterface.getChatList(user)
    }

}