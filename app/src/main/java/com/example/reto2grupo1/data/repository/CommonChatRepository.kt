package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.DbChat
import com.example.reto2grupo1.utils.Resource

interface CommonChatRepository {
    suspend fun getChats():Resource<List<Chat>>
    suspend fun createChat(chat: Chat) : Resource<Int>
    suspend fun deleteChat(chat: Chat): Resource<Void>
}