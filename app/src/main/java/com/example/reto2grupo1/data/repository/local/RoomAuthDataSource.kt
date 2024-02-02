package com.example.reto2grupo1.data.repository.local


import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.DbChat
import com.example.reto2grupo1.data.DbMessage
import com.example.reto2grupo1.data.repository.CommonAuthRepository
import com.example.reto2grupo1.utils.Resource

class RoomAuthDataSource : CommonAuthRepository {
    private val authDao : AuthDao = MyApp.db.authDao()

    override suspend fun login(email: String, pass:String): Boolean {
       try {

           if(authDao.login(email, pass)>0){
               changeToLogged(email)
               return true
           }
           return false
       } catch (ex: SQLiteConstraintException) {
           return false
       }
    }

    override suspend fun changeToLogged(email: String) {
        authDao.changeToLogged(email)
    }

    override suspend fun changeToUnLogged() {
        authDao.changeToUnLogged()
    }

    override suspend fun saveUser(email: String, pass: String) {
        changeToUnLogged()
        changeToLogged(email)
        authDao.saveUser(email, pass)

    }


}
@Dao
interface AuthDao {
    @Query("SELECT  COUNT(*)  FROM login where email = :email and password = :pass")
    suspend fun login(email: String, pass: String): Int

    @Query("UPDATE users SET logged = 1 WHERE email = :email")
    suspend fun changeToLogged(email: String)

    @Query("UPDATE users SET logged = 0 WHERE logged = 1")
    suspend fun changeToUnLogged()

    @Query("INSERT OR IGNORE INTO login (email, password) VALUES (:email, :pass)")
    suspend fun saveUser(email: String, pass: String) :Long
}