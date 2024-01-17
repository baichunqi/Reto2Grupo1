package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AuthenticationRequest (
    val email: String,
    val password: String
): Parcelable