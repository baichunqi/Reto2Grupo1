package com.example.reto2grupo1.ui.createGroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.CreateGroupRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreateGroupViewModelFactory(
    private val createGroupRepository: CreateGroupRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return CreateGroupViewModel(createGroupRepository) as T
    }
}
class CreateGroupViewModel (
    private val createGroupRepository: CreateGroupRepository
) : ViewModel(){

    private val _createChatResult = MutableLiveData<Resource<Void>>()
    val createChatResult: LiveData<Resource<Void>> get() = _createChatResult

    fun createChat(chat : Chat){
        viewModelScope.launch{
            val repoResponse = createChatRepo(chat)
            _createChatResult.value = repoResponse
        }
    }

    suspend fun createChatRepo(chat : Chat) : Resource<Void> {
        return withContext(Dispatchers.IO){
            createGroupRepository.createChat(chat)
        }
    }
}
