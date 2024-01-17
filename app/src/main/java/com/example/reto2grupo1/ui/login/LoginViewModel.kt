package com.example.reto2grupo1.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.AuthenticationResponse
import com.example.reto2grupo1.data.repository.AuthenticationRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {


    private val _login = MutableLiveData<Resource<AuthenticationResponse>>()
    val login : LiveData<Resource<AuthenticationResponse>> get() = _login

    fun onLoginClick(email: String, password: String) {
        val authenticationRequest = AuthenticationRequest(email, password)
        viewModelScope.launch {
            _login.value = login(authenticationRequest)
        }
    }

    private suspend fun login(authenticationRequest: AuthenticationRequest): Resource<AuthenticationResponse>{
        return withContext(Dispatchers.IO) {
            authenticationRepository.login(authenticationRequest)
        }
    }


}

class LoginViewModelFactory(
    private val authenticationRepository: AuthenticationRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return LoginViewModel(authenticationRepository) as T
    }
}