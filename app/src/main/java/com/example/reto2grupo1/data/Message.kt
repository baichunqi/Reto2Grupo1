package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Message (
    val id: Int,
    val messageContent : String,
    val date : Date,
): Parcelable