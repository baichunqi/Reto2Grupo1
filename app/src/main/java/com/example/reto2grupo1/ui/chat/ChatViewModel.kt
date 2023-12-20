package com.example.reto2grupo1.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModelFactory(
    private val chatRepository: ChatRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return ChatViewModel(chatRepository) as T
    }
}
class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    fun getChatContent(chat : Chat){
        viewModelScope.launch {
            getChatMessages(chat)
        }
    }
    suspend fun getChatMessages(chat : Chat){
        return withContext(Dispatchers.IO){
            chatRepository.getChat(chat)
        }
    }
}