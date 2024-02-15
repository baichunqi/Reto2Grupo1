package com.example.reto2grupo1.ui.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.ChatListRepository
import com.example.reto2grupo1.data.repository.local.RoomChatDataSource
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatListViewModelFactory(
    private val chatListRepository: ChatListRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return ChatListViewModel(chatListRepository) as T
    }
}

class ChatListViewModel(
    private val chatListRepository: ChatListRepository
) : ViewModel(){
    private var _chats = MutableLiveData<Resource<List<Chat>>>()
    private lateinit var localChatRepository:RoomChatDataSource
    val chats : LiveData<Resource<List<Chat>>> get() = _chats

    private val _deleted = MutableLiveData<Resource<Void>>()
    val deleted : MutableLiveData<Resource<Void>> get() = _deleted
    
    init{
        getChats()
    }

    fun getChats() {
        viewModelScope.launch {
            val repoResponse = getUserChatList()
//                for (chat in repoResponse ){
//                    withContext(Dispatchers.IO){
//                        localChatRepository.createChat(chat)
//                    }
//                }


            _chats.value = repoResponse
        }

    }
     private suspend fun getUserChatList(): Resource<List<Chat>> {
         return withContext(Dispatchers.IO) {
             chatListRepository.getChatList()
         }
     }

    fun onDeleteChat(chatId: Int){
        viewModelScope.launch {
            _deleted.value = deleteChat(chatId)
        }


    }

    suspend fun deleteChat(chatId: Int): Resource<Void>{
        return withContext(Dispatchers.IO) {
            chatListRepository.deleteChat(chatId)
        }
    }


 }
