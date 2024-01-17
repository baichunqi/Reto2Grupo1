package com.example.reto2grupo1.ui.register

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2grupo1.data.PassChange
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

    suspend fun uploadPhoto(file: File) {
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
                    Log.i("pruebas", "Error al subir foto")
                }
            } catch (e: Exception) {
                Log.e("UploadPhoto", "Error: ${e.message}", e)
            }
        }

    }
}