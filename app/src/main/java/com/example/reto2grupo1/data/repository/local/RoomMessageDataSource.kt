package com.example.reto2grupo1.data.repository.local

import android.database.sqlite.SQLiteConstraintException
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

    override suspend fun getChatMessages(id : Int) : Resource<List<Message>>{
        val response = messageDao.getMessages(id).map { it.toMessage() }
        return Resource.success(response)
    }

    override suspend fun createMessage(message: Message): Resource<Void> {
        try {
            messageDao.addMessage(message.toDbMessage())
            return Resource.success()
        } catch (ex:SQLiteConstraintException){
            return Resource.error(ex.message!!)
        }
    }

}

fun Message.toDbMessage() = DbMessage(id, text, userId, chatId)
fun DbMessage.toMessage() = Message(id, text, userId, chatId)

@Dao
interface MessageDao{
    @Query("Select * from messages order by id asc")
    suspend fun getMessages(id: Int): List<DbMessage>

    @Insert
    suspend fun addMessage(message: DbMessage) :Long
}
