package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.utils.Resource

interface CreateGroupRepository {
    suspend fun createChat(name : String, private : Boolean) : Resource<Boolean>
}