package com.example.reto2grupo1.data.repository.remote

import android.util.Log
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.ChatListRepository
import com.example.reto2grupo1.utils.Resource

class RemoteChatListDataSource : BaseDataSource(), ChatListRepository {
    override suspend fun getChatList(): Resource<List<Chat>> = getResult {
        RetrofitClient.apiInterface.getChats()
    }

    override suspend fun getAllPublicChat() = getResult {
        RetrofitClient.apiInterface.getAllPublicChats()
    }
    override suspend fun joinChat(chatId: Int?) = getResult {
        RetrofitClient.apiInterface.joinChat(chatId)
    }

    override suspend fun deleteChat(chatId: Int) = getResult {
        RetrofitClient.apiInterface.deleteChat(chatId)
    }
}