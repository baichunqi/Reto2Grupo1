package com.example.reto2grupo1.ui.register


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.PassChange
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.repository.AuthenticationRepository
import com.example.reto2grupo1.data.repository.UserRepository
import com.example.reto2grupo1.data.repository.remote.RetrofitClient
import com.example.reto2grupo1.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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


    fun uploadPhotoToServer(file:File){
        viewModelScope.launch{
            uploadPhoto(file)
        }
    }

    suspend fun uploadPhoto(file: File) {
        Log.i("pruebas", "ha entrado")
        return withContext(Dispatchers.IO){
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            // Llama al método de la interfaz API para subir la foto
            try {
                val response = RetrofitClient.apiInterface.uploadPhoto(photoPart)
                // Maneja la respuesta del servidor según tus necesidades
                if (response.isSuccessful) {
                    Log.i("pruebas", "Se ha subido con exito")
                } else {
                    Log.i("pruebas", response.toString())
                    Log.i("pruebas", "Error al subir foto")
                }
            } catch (e: Exception) {
                Log.i("errorPruebas", e.toString())
            }
        }

    }

}