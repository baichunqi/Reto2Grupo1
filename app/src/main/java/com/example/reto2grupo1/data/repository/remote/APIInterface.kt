package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.AuthenticationResponse
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.User
import retrofit2.http.Body
import retrofit2.http.GET
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface APIInterface {

    @GET("user")
    suspend fun getUser(): Response<User>
    @PUT("password")
    suspend fun changePass(@Body passChange: PassChange):Response<Int>
    @GET("chatList")
    suspend fun getChatList(@Body user: User): Response<List<Chat>>
    @GET("chat")
    suspend fun getChatContent(@Body chat: Chat): Response<List<Message>>


    @POST("auth/login")
    suspend fun login(@Body authenticationRequest : AuthenticationRequest): Response<AuthenticationResponse>
    @GET("auth/myInfo")
    suspend fun myInfo(): Response<User>

    @Multipart
    @POST("uploadPhoto")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): Response<String>


}