package com.example.reto2grupo1.ui.register

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.reto2grupo1.data.repository.remote.RetrofitClient
import com.example.reto2grupo1.databinding.ActivityRegisterBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

class RegisterActivity : ComponentActivity() {

    private var selectedImage: File? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var defaultPass : Boolean = false
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                defaultPass = false
            } else {
                defaultPass = extras.getBoolean("defaultPass")
            }
        }

        if (!defaultPass) {
            binding.editTextNombre.isEnabled = false
            binding.editTextApellido.isEnabled = false
            binding.editTextDNI.isEnabled = false
            binding.spinnerCicloFormativo.isEnabled = false
            binding.editTextCurso.isEnabled = false
            binding.checkBoxFCTDUAL.isEnabled = false
            binding.buttonRegistro.isVisible = false

            binding.imageView4.setOnClickListener() {
                val intent = Intent(this, ChatListActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            binding.buttonCambioDeContraseA.isVisible = false

            binding.imageView4.setOnClickListener() {
                Toast.makeText(this, "@string/revisarCampos", Toast.LENGTH_SHORT).show()
            }
        }

        var startActivityIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> {
                if (it.resultCode == AppCompatActivity.RESULT_OK) {
                    val imageBitmap = it.data?.extras?.getParcelable("data", Bitmap::class.java)
                    imageBitmap?.let {
                        binding.imageView5.setImageBitmap(imageBitmap)
                        selectedImage = saveBitmapToFile(it)
                    } ?: run {
                        Toast.makeText(this, "@string/noSePudoObtenerImagen", Toast.LENGTH_LONG).show()
                    }
                }
            })



        fun dispatchTakePictureIntent() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityIntent.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT)
            }
        }

        binding.buttonCambiarFoto.setOnClickListener {
            dispatchTakePictureIntent()
            selectedImage?.let { it1 -> uploadPhoto(it1) }
        }

    }
    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File.createTempFile("image", ".jpg", cacheDir)
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()

        return file
    }



}