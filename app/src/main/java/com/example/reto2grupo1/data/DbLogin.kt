package com.example.reto2grupo1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="login")
data class DbLogin(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val email: String,
    val password: String,
)
