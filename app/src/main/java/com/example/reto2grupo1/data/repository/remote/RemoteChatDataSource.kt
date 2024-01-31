package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.repository.ChatRepository
import com.example.reto2grupo1.utils.Resource

class RemoteChatDataSource : BaseDataSource(), ChatRepository {
    override suspend fun getChat(chat: Chat): Resource<List<Message>> = getResult {
        RetrofitClient.apiInterface.getChatContent(chat)
    }
    override suspend fun getChatMessages(id : Int): Resource<List<Message>> = getResult {
        RetrofitClient.apiInterface.getMessagesFromChat(id)
    }
    override suspend fun assignUser(chatId: Int, userId: Int): Resource<Void> = getResult {
        RetrofitClient.apiInterface.assignUser(chatId, userId)
    }
}