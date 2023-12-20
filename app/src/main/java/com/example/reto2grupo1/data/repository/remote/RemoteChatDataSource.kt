package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.ChatRepository

class RemoteChatDataSource : BaseDataSource(), ChatRepository {
    override suspend fun getChat(chat: Chat) {
        RetrofitClient.apiInterface.getChatContent(chat)
    }
}