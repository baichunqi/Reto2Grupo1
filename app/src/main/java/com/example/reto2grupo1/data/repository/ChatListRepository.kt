package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.utils.Resource

interface ChatListRepository {
     suspend fun getChatList(): Resource<List<Chat>>
     suspend fun getAllPublicChat(): Resource<List<Chat>>
     suspend fun joinChat( chatId :Int): Resource<Boolean>

}