package com.example.reto2grupo1.ui.createGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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

    fun createChat(name : String, private : Boolean){
        viewModelScope.launch{
            val repoResponse = createChatRepo(name, private)
        }
    }

    suspend fun createChatRepo(name : String, private : Boolean) : Resource<Boolean> {
        return withContext(Dispatchers.IO){
            createGroupRepository.createChat(name, private)
        }
    }
}
