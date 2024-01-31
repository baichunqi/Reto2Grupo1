package com.example.reto2grupo1.ui.AddUser

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.AuthenticationRepository
import com.example.reto2grupo1.data.repository.ChatRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddUserModelFactory(
    private val authenticationRepository : AuthenticationRepository,
    private val chatRepository: ChatRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass : Class<T>, extras : CreationExtras):T{
        return AddUserViewModel(authenticationRepository, chatRepository) as T
    }
}
class AddUserViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _users = MutableLiveData<Resource<List<User>>>()

    val users: LiveData<Resource<List<User>>> get() = _users

    private val _join = MutableLiveData<Resource<Void>>()
    val join : LiveData<Resource<Void>> get() = _join

    init{
        getUsers()
    }
    fun getUsers(){
        viewModelScope.launch {
            val repoResponse = getUsersRepo()
            _users.value = repoResponse
        }
    }

    suspend fun getUsersRepo() : Resource<List<User>>{
        return withContext(Dispatchers.IO){
            authenticationRepository.getAllUsers()
        }
    }

    fun addUser(chatId : Int, userId : Int) {
        viewModelScope.launch {
            Log.i("Prueba añadir", chatId.toString())
            val repoResponse = joinUser(chatId, userId)
            _join.value = repoResponse
        }
    }
    suspend fun joinUser(chatId : Int, userId: Int) : Resource<Void>{
        return withContext(Dispatchers.IO){
            Log.i("Prueba añadir", "llama repo")
            chatRepository.assignUser(chatId, userId)
        }
    }
}