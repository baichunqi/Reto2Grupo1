package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthenticationResponse (


    val email: String,
    val accessToken: String,
): Parcelable