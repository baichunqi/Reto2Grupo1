package com.example.reto2grupo1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class DbUser (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val email:String,
    val name: String,
    val surname:String,
    val phone:Int,
    val dni:String,
    val address:String,
)