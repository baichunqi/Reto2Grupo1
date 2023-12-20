package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface APIInterface {

    @GET("user")
    suspend fun getUser(): Response<User>
    @PUT("password")
    suspend fun changePass(@Body passChange: PassChange):Response<Int>
    @GET("chatList")
    suspend fun getChatList(@Body user: User): Response<List<Chat>>
    @GET("chat")
    suspend fun getChatContent(@Body chat: Chat): Response<List<Message>>
}