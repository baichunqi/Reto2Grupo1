package com.example.reto2grupo1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="chats")
data class DbChat(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val name : String,
    val message : String?,
    val privateChat : Boolean,
)
