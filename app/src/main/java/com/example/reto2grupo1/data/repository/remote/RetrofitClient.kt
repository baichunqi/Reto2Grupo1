package com.example.reto2grupo1.data.repository.remote

import android.util.Log
import com.example.reto2grupo1.MyApp
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

object RetrofitClient {

    //      const val API_URI = "http://10.0.2.2:8080/api/"
    const val API_URI = "http://10.5.7.13:8081/api/"
    //const val API_URI = "http://10.5.7.202:80/api/"


    var client = OkHttpClient.Builder().addInterceptor { chain ->
        val authToken = MyApp.userPreferences.fetchAuthToken()

        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        chain.proceed(newRequest)
    }.build()


    val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(API_URI)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiInterface: APIInterface by lazy {
        retrofitClient
            .build()
            .create(APIInterface::class.java)
    }

}