package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.utils.Resource

interface CommonChatRepository {
    suspend fun getChats():Resource<List<Chat>>
    suspend fun getAllChats():Resource<List<Chat>>
    suspend fun createChat(chat: Chat) : Resource<Int>
}