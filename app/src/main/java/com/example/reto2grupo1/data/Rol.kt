package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize



@Parcelize
data class Rol (
    val id : Int?,
    val name : String,
) : Parcelable