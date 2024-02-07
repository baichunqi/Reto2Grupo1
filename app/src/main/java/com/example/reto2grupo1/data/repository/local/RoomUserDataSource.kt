package com.example.reto2grupo1.data.repository.local

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.DbUser
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.CommonUserRepository
import com.example.reto2grupo1.utils.Resource

class RoomUserDataSource : CommonUserRepository{
    private val userDao : UserDao = MyApp.db.userDao()
    private val authDao : AuthDao = MyApp.db.authDao()


    override suspend fun showInfo(): Resource<User> {

        val response = userDao.showInfo(getLoggedEmail().toString())
        val user = response?.toUser()

        return if (user != null) {
            Resource.success(user)
        } else {
            Resource.error("User not found", null)
        }
    }

    override suspend fun getLoggedEmail(): String {

        Log.d("getChatLists"," userDao.getLoggedEmail()")
        var response = userDao.getLoggedEmail()
        Log.d("getChatLists"," response")
        Log.d("getChatLists", response.toString())
        return response
    }

    override suspend fun addUser(user: User): Long {
        var dbUser = user.toDbUser()
        if (dbUser != null) {
            authDao.changeToLogged(dbUser.email.toString())
        }
        return userDao.addUser(dbUser)
    }
    override suspend fun getUsers(): Resource<List<User>> {

        var response = userDao.getChats()
        return Resource.success(response)
    }

    override suspend fun getLoggedId(): String {
        return userDao.getLoggedId().toString()
    }

    fun User.toDbUser() = id?.let { DbUser(it, email, name, surname, phone, dni, address, false) }
    fun DbUser.toUser() = id?.let { User(it, email, name, surname, phone, dni, address) }
}

@Dao
interface UserDao {

    @Query("SELECT * FROM users order by id asc")
    suspend fun getChats(): List<User>
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun showInfo(email:String): DbUser

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: DbUser?):Long

    @Query("SELECT email FROM users WHERE logged = 1")
    suspend fun getLoggedEmail(): String

    @Query("SELECT id FROM users WHERE logged = 1")
    suspend fun getLoggedId(): Int



}
