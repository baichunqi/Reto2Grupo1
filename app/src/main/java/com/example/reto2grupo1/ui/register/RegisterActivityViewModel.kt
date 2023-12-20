package com.example.reto2grupo1.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.repository.UserRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivityViewModelFactory(
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return RegisterViewModel(userRepository) as T
    }
}

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel(){

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
}