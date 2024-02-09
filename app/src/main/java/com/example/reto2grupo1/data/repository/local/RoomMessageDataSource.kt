package com.example.reto2grupo1.data.repository.local

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reto2grupo1.MyApp
import com.example.reto2grupo1.data.DbMessage
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.repository.CommonMessageRepository
import com.example.reto2grupo1.utils.Resource

class RoomMessageDataSource : CommonMessageRepository{
    private val messageDao: MessageDao = MyApp.db.messageDao()
    private val userDao : UserDao = MyApp.db.userDao()
    override suspend fun getChatMessages(id : Int) : Resource<List<Message>>{
        val response = messageDao.getMessages(id, userDao.getLoggedEmail()).map { it.toMessage() }
        return Resource.success(response)

    }

    override suspend fun createMessage(message: Message): Resource<Void> {
        try {
            messageDao.addMessage(message.toDbMessage(userDao.getLoggedEmail(), true))
            return Resource.success()
        } catch (ex:SQLiteConstraintException){
            ex.message?.let { Log.e("FFF", it) }
            return Resource.error(ex.message!!)
        }
    }

    override suspend fun createOfflineMessage(message: Message): Resource<Void> {

        try {
            messageDao.addMessage(message.toDbMessage(userDao.getLoggedEmail(), false))
            return Resource.success()
        } catch (ex:SQLiteConstraintException){
            ex.message?.let { Log.e("FFF", it) }
            return Resource.error(ex.message!!)
        }
    }

    override suspend fun changeToSent(message: Message){
        message.id?.let { messageDao.changeToSent(it) }
    }

    override suspend fun deleteMessagesById(id: Int) {
        messageDao.clearMessage(id)
    }

    override suspend fun getUnsendedMessages(): Resource<List<Message>> {
        val response = messageDao.getUnsendedMessages().map { it.toMessage() }
        return Resource.success(response)
    }

}

fun Message.toDbMessage(userEmail: String, sendToServer: Boolean) = DbMessage(id, text, userId, chatId, userEmail, sendToServer, created_at.toString())
fun DbMessage.toMessage() = Message(id, text, userId, chatId, time)

@Dao
interface MessageDao{
    @Query("Select * from messages WHERE chatId=:id and userEmail = :userEmail order by id asc")
    suspend fun getMessages(id: Int, userEmail:String): List<DbMessage>

    @Insert
    suspend fun addMessage(message: DbMessage) :Long

    @Query("DELETE FROM messages where id = :id")
    suspend fun clearMessage(id:Int)

    @Query("UPDATE messages SET sendToServer = 1 WHERE id = :id")
    suspend fun changeToSent(id: Int)

    @Query("select * from messages where sendToServer = 0")
    suspend fun getUnsendedMessages(): List<DbMessage>


}
