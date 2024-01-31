package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.utils.Resource

interface CreateGroupRepository {
    suspend fun createChat(chat : Chat) : Resource<Void>
}