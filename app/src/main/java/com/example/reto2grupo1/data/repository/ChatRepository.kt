package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Chat

interface ChatRepository {
    suspend fun getChat(chat : Chat)
}