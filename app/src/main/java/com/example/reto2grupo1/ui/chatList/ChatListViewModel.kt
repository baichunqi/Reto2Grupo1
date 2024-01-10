package com.example.reto2grupo1.ui.chatList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.ChatListRepository
import com.example.reto2grupo1.data.repository.remote.RemoteChatDataSource
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
 fun getChatList(user : User){
     viewModelScope.launch {
         getUserChatList(user)
     }
 }

    suspend fun getUserChatList(user: User) {
    return withContext(Dispatchers.IO){
            chatListRepository.getChatList(user)
        }
    }
}