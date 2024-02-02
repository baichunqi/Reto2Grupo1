package com.example.reto2grupo1.data.repository.local

import androidx.room.Dao
import androidx.room.Query
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.DbUser
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.CommonAuthRepository
import com.example.reto2grupo1.utils.Resource

class RoomUserDataSource : CommonAuthRepository{
    //private val userDao : UserDao = MyApp.db.userDao()



    override suspend fun myInfo(): Resource<User> {
        TODO("Not yet implemented")
    }
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = '?'")
    suspend fun showInfo(): UserDao

}
