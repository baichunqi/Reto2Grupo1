package com.example.reto2grupo1.data.repository.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.DbChat
import com.example.reto2grupo1.data.repository.CommonChatRepository
import com.example.reto2grupo1.utils.Resource

abstract class RoomChatDataSource : CommonChatRepository {
    private val chatDao : ChatDao = MyApp.db.chatDao()
    override suspend fun getChats(): Resource<List<Chat>> {
        val response = chatDao.getChats().map { it.toChat() }
        return Resource.success(response)
    }

    override suspend fun createChat(chat: Chat): Resource<Int> {
        val response = chatDao.addChat(chat.toDbChat())
        return Resource.success(response.toInt())
    }

    suspend fun deleteChat(chat: Chat): Resource<Void> {
        return try {
            chatDao.deleteChat(chat.toDbChat()) // Convierte Chat a DbChat antes de llamar al DAO
            Resource.success()
        } catch (ex: SQLiteConstraintException) {
            Resource.error(ex.message!!)
        }
    }


}
fun Chat.toDbChat() = DbChat(id, name, message , private)
fun DbChat.toChat() = Chat(id, name, "", privateChat)

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats order by id asc")
    suspend fun getChats(): List<DbChat>

    @Insert
    suspend fun addChat(chat:DbChat):Long

    @Delete
    suspend fun deleteChat(chat: DbChat) : Int

}
