package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.User

interface ChatListRepository {
     suspend fun getChatList(user: User)
}