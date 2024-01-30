package com.example.reto2grupo1.ui.AddUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.AuthenticationRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddUserModelFactory(
    private val authenticationRepository : AuthenticationRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass : Class<T>, extras : CreationExtras):T{
        return AddUserViewModel(authenticationRepository) as T
    }
}
class AddUserViewModel(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    private val _users = MutableLiveData<Resource<List<User>>>()

    val users: LiveData<Resource<List<User>>> get() = _users

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
}