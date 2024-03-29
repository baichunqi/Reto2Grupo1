package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.AuthenticationResponse
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.Rol
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.UserUpdate
import com.example.reto2grupo1.data.UserWithRol
import retrofit2.http.Body
import retrofit2.http.GET
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIInterface {

    @GET("user")
    suspend fun getUser(@Query("chatId") id : Int): Response<User>
    @GET("users/chat")
    suspend fun getChatUsers(id: Int): Response<List<User>>
    @PUT("password")
    suspend fun changePass(@Body passChange: PassChange):Response<Int>
    @GET("chats/userChats")
    suspend fun getChatList(@Body user: User): Response<List<Chat>>
    @GET("chat")
    suspend fun getChatContent(@Body chat: Chat): Response<List<Message>>
    @GET("messages/{chatId}")
    suspend fun getMessagesFromChat(@Path("chatId") id :Int) : Response<List<Message>>

    @POST("auth/login")
    suspend fun login(@Body authenticationRequest : AuthenticationRequest): Response<AuthenticationResponse>
    @GET("auth/myInfo")
    suspend fun myInfoUserWhitRol(): Response<UserWithRol>
    @GET("auth/myInfo")
    suspend fun myInfo(): Response<User>

    @GET("send-email")
    suspend fun changePassword(
        @Query("toEmail") toEmail : String,
        @Query("subject") subject : String,
        @Query("message") message : String
    ): Response<Void>

    @PUT("auth/users")
    suspend fun update(@Body user: UserWithRol): Response<UserWithRol>

    @GET("auth/users")
    suspend fun getAllUsers() : Response<List<User>>

    @GET("chats")
    suspend fun getChats(): Response<List<Chat>>
    @POST("chats/join")
    suspend fun joinChat(@Query("chatId") chatId: Int?) : Response<Boolean>


    @POST("chats/assign")
    suspend fun assignUser(@Query("chatId")chatId: Int,@Query("userId") userId: Int): Response<Void>

    @DELETE("chats/leave")
    suspend fun leaveChat(@Query("chatId")chatId: Int):Response<Boolean>

    @POST("chats")
    suspend fun createChat(@Body chat : Chat) : Response<Void>

    @Multipart
    @POST("uploadPhoto")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): Response<String>

    @GET("chats/public")
    suspend fun getAllPublicChats(): Response<List<Chat>>

    @DELETE("chats/delete")
    suspend fun deleteChat(@Query("chatId") chatId: Int): Response<Void>

    @GET("users/chat")
    suspend fun getUsersChat(@Query("chatId") chatId: Int): Response<List<User>>

    @GET("user/rol")
    suspend fun getUserRol(): Response<Rol>

    

    @DELETE("chats/disassign")
    suspend fun dissasingUser(@Query("chatId") chatId: Int,@Query("userId")userId: Int) : Response<Void>

    @GET("lastMessages")
    suspend fun getLastMessages(@Query("created_at") date: String): Response<List<Message>>

}