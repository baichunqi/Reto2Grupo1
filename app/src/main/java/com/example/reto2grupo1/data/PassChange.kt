package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PassChange(
    var contrasenyaOld : String,
    var contrasenya: String,
): Parcelable
