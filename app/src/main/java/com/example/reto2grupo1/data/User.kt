package com.example.reto2grupo1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    val id : Int,
    val email:String,
    val name: String,
    val surname:String,
    val phone:Int,
    val dni:String,
    val address:String,
) : Parcelable


@Parcelize
data class UserUpdate(
    var email:String,
    val name: String,
    val surname:String,
    val password:String,
    val phone:Int,
    val dni:String,
    val address:String):Parcelable
