package com.example.reto2grupo1.ui.showUsers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.ChatRepository
import com.example.reto2grupo1.data.repository.UserRepository
import com.example.reto2grupo1.data.repository.remote.RemoteUserDataSource
import com.example.reto2grupo1.ui.chat.ChatViewModel
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowUserViewModelFactory(
    private val userRepository: RemoteUserDataSource
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return ShowUserViewModel(userRepository) as T
    }
}
class ShowUserViewModel(
    private val userRepository: RemoteUserDataSource
) : ViewModel() {
    private val _users = MutableLiveData<Resource<List<User>>>()
    val users: LiveData<Resource<List<User>>> get() = _users


    fun getList(id: Int){
        viewModelScope.launch{
            val response = getUsers(id)
            _users.value = response
        }
    }
    private suspend fun getUsers(id: Int) : Resource<List<User>>{
        return withContext(Dispatchers.IO){
            Log.d("Lista user",     userRepository.getChatUsers(id).data.toString())
            userRepository.getChatUsers(id)
        }
    }
}