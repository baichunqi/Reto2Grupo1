package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Message (
    val id: Integer,
    val text : String,
    val user_id : String,
    val date : Date,
): Parcelable