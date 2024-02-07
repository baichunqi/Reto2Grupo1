package com.example.reto2grupo1.data.repository

import com.example.reto2grupo1.utils.Resource

interface CommonEmailRepository {
    suspend fun sendEmail(toEmail: String, subject: String, message: String) : Resource<Void>
}