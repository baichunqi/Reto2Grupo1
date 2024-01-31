package com.example.reto2grupo1.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reto2grupo1.data.DbChat
import com.example.reto2grupo1.data.DbMessage


@Database(
    entities = [DbChat::class, DbMessage::class],
    version = 5,
    exportSchema = false
)

abstract class MyAppRoomDataBase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao() : MessageDao
}