package com.example.reto2grupo1.ui.changePassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.CommonEmailRepository
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordViewModelFactory(
    private val emailRepository: CommonEmailRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras : CreationExtras):T{
        return ChangePasswordViewModel(emailRepository) as T
    }
}
class ChangePasswordViewModel (
    private val emailRepository: CommonEmailRepository
) : ViewModel(){

    private val _sendEmail = MutableLiveData<Resource<Void>>()
    val sendEmail: LiveData<Resource<Void>> get() = _sendEmail
    fun sendEmail(toEmail : String, subject: String, message: String) {
        viewModelScope.launch{
            val repoResponse = onSendEmail(toEmail, subject, message)
        }


    }
    suspend fun onSendEmail(toEmail : String, subject: String, message: String) : Resource<Void> {
        return withContext(Dispatchers.IO){
            emailRepository.sendEmail(toEmail, subject, message)
        }
    }
}