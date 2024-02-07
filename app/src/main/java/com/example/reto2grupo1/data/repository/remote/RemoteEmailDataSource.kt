package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.repository.CommonEmailRepository

class RemoteEmailDataSource : BaseDataSource(), CommonEmailRepository {
    override suspend fun sendEmail(toEmail: String, subject: String, message: String) = getResult{
        RetrofitClient.apiInterface.changePassword(toEmail, subject, message)
    }

}