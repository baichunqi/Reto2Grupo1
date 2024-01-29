package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat (
    val id : Int,
    val name : String,
    val message : String,
    val private : Boolean,
) : Parcelable