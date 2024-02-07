package com.example.reto2grupo1.ui.showUsers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.Rol
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.remote.RemoteUserDataSource
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

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
    private val _rol = MutableLiveData<Resource<Rol>>()
    val rol: LiveData<Resource<Rol>> get() = _rol

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


    fun getRol()  {
        viewModelScope.launch {
            var rep : Resource<Rol> = userRol()
            _rol.value = rep
            Log.d("Rol",rep.data?.name.toString())
        }
    }

    suspend fun userRol(): Resource<Rol>{
        return withContext(Dispatchers.IO){
            userRepository.getUserRol()
        }
    }

    fun dissassing(chatId: Int, userId: Int){
        viewModelScope.launch {
            dissAssingUser(chatId,userId)
        }
    }
     suspend fun dissAssingUser(chatId:Int, userId:Int){
        return withContext(Dispatchers.IO){
            Log.d("utiles 1", chatId.toString() + userId.toString())
            userRepository.dissAssingUser(chatId,userId)
        }
    }

}