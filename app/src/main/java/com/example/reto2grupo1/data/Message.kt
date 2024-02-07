package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Message(
    val id: Int?,
    val text: String,
    val userId: String,
    val chatId: String,
    val created_at: String

    ): Parcelable