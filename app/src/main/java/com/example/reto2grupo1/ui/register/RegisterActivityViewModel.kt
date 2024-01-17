package com.example.reto2grupo1.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.AuthenticationResponse
import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.AuthenticationRepository
import com.example.reto2grupo1.data.repository.UserRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivityViewModelFactory(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return RegisterViewModel(userRepository ,authenticationRepository) as T
    }
}


class RegisterViewModel(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository

) : ViewModel(){
    private val _user = MutableLiveData<Resource<User>>()
    val user : LiveData<Resource<User>> get() = _user
    fun changePassUser(passChange: PassChange){
        viewModelScope.launch{
            changePass(passChange)
        }
    }
    suspend fun changePass(passChange : PassChange) : Resource<Int> {
        return withContext(Dispatchers.IO){
            userRepository.changePass(passChange)
        }
    }


    fun takeInfo() {
        viewModelScope.launch {
            _user.value = showInfo()
        }
    }
    suspend fun showInfo() : Resource<User> {
       return withContext(Dispatchers.IO){
           authenticationRepository.myInfo()
       }
    }

}