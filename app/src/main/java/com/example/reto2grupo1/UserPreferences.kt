package com.example.reto2grupo1


import android.content.Context
import android.content.SharedPreferences


class UserPreferences() {
    private val sharedPreferences: SharedPreferences by lazy {
        MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_EMAIL="email"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthTokenWithPs( email:String, token: String) {
        val editor = sharedPreferences.edit()


        editor.putString(USER_EMAIL, email)
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    fun restartPreference (){
        val  editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    fun saveAuthToken(email:String, token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_EMAIL, email)
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthLogin():String?{
        return sharedPreferences.getString(USER_EMAIL, null)
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return sharedPreferences.getString(USER_TOKEN, null)
    }
}