package com.example.reto2grupo1.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reto2grupo1.data.DbChat
import com.example.reto2grupo1.data.DbLogin
import com.example.reto2grupo1.data.DbMessage
import com.example.reto2grupo1.data.DbUser


@Database(
    entities = [DbChat::class, DbMessage::class, DbUser::class, DbLogin::class],
    version = 9,
    exportSchema = false
)

abstract class MyAppRoomDataBase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao() : MessageDao
    abstract fun authDao(): AuthDao
    abstract fun userDao(): UserDao


}