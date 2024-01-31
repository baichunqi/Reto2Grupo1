package com.example.reto2grupo1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="messages")
data class DbMessage(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val text: String,
    val userId: String,
    val chatId: String,
)
