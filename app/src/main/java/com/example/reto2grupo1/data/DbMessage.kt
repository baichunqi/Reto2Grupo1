package com.example.reto2grupo1.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName ="messages")
data class DbMessage(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val text: String,
    val userId: String,
    val chatId: String,
    val userEmail: String,
    val sendToServer : Boolean,
//    val time: Date,
)
