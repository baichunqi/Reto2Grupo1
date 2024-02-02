package com.example.reto2grupo1.data.repository.local

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.DbChat
import com.example.reto2grupo1.data.repository.CommonChatRepository
import com.example.reto2grupo1.utils.Resource
import kotlin.math.log

class RoomChatDataSource : CommonChatRepository {
    private val chatDao : ChatDao = MyApp.db.chatDao()
    private val userDao : UserDao = MyApp.db.userDao()
    override suspend fun getChats(): Resource<List<Chat>> {
        Log.d("chatstest",userDao.getLoggedEmail())
        val response = chatDao.getChats(userDao.getLoggedEmail()).map { it.toChat() }
        return Resource.success(response)
    }

    override suspend fun getAllChats(): Resource<List<Chat>> {
        var response = chatDao.getAllChats().map { it.toChat() }
        return Resource.success(response)
    }

    override suspend fun createChat(chat: Chat): Resource<Int> {
        val response = chatDao.addChat(chat.toDbChat(userDao.getLoggedEmail()))
        return Resource.success(response.toInt())
    }

    override suspend fun deleteChat(chat: Chat): Resource<Void> {
        try {
            chatDao.deleteChat(chat.toDbChat(userDao.getLoggedEmail())) // Convierte Chat a DbChat antes de llamar al DAO
            return Resource.success()
        } catch (ex: SQLiteConstraintException) {
            return Resource.error(ex.message!!)
        }
    }


}
fun Chat.toDbChat(userEmail:String) = DbChat(id, name, message , private, userEmail)
fun DbChat.toChat() = Chat(id, name, "", privateChat)

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats where userEmail = :email order by id asc")
    suspend fun getChats(email: String): List<DbChat>

    @Query("SELECT * FROM chats order by id asc")
    suspend fun getAllChats(): List<DbChat>

    @Insert
    suspend fun addChat(chat:DbChat):Long

    @Delete
    suspend fun deleteChat(chat: DbChat) : Int

}
