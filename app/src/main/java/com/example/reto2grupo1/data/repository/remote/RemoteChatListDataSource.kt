package com.example.reto2grupo1.data.repository.remote

import android.util.Log
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.ChatListRepository
import com.example.reto2grupo1.utils.Resource

class RemoteChatListDataSource : BaseDataSource(), ChatListRepository {
    override suspend fun getChatList() = getResult {
        RetrofitClient.apiInterface.getChats()
    }

    override suspend fun getAllPublicChat(): Resource<List<Chat>>  = getResult {
        RetrofitClient.apiInterface.getAllPublicChats()
    }

    override suspend fun joinChat(chatId: Int): Resource<Boolean> = getResult {
        Log.d("estoentra6","estoentra6");

        RetrofitClient.apiInterface.joinChat(chatId)
    }
}