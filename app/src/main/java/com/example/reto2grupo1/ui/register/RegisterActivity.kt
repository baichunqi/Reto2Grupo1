package com.example.reto2grupo1.ui.register

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.UserUpdate
import com.example.reto2grupo1.data.repository.remote.RemoteAuthenticationRepository
import com.example.reto2grupo1.data.repository.remote.RemoteUserDataSource
import com.example.reto2grupo1.databinding.ActivityRegisterBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.utils.Resource
import java.io.File
import java.io.FileOutputStream


class RegisterActivity : ComponentActivity() {
    private val authenticationRepository = RemoteAuthenticationRepository();
    private val userRepository = RemoteUserDataSource();


    private val viewModel: RegisterViewModel by viewModels {
        RegisterActivityViewModelFactory(
            userRepository,authenticationRepository
        )
    }

    private var selectedImage: File? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data2 = viewModel.takeInfo()
        Log.e("prueba", data2.toString())

        viewModel.user.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let { data ->
                        Log.e("Antes de guardar", data.toString())
                        binding.editTextLogin.setText(data.email)
                        binding.editTextNombre.setText(data.name)
                        binding.editTextApellido.setText(data.surname)
                        binding.editTextDNI.setText(data.dni)
                        binding.editTextDirecciN.setText(data.address)
                        binding.editTextTelefono1.setText(data.phone.toString())

                    }
                }
                Resource.Status.ERROR -> {


                }
                Resource.Status.LOADING -> {
                    // de momento
                }
            }})


        var defaultPass : Boolean = false
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                defaultPass = false

            } else {
                defaultPass = extras.getBoolean("defaultPass")
                Log.i("pass", defaultPass.toString())
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
                Log.i("pruebas", "wdentr?")
                selectedImage?.let { it1 -> viewModel.uploadPhotoToServer(it1)}
                Log.i("pruebas", "wdentrr?")
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT)
            }
        }

        binding.buttonCambiarFoto.setOnClickListener {
            Log.i("pruebas", "entr?")
            dispatchTakePictureIntent()
        }


        binding.buttonCambioDeContraseA.setOnClickListener {
            if (binding.editTextContraseA.text.toString() == binding.editTextRepetirContraseA.text.toString()) {
                viewModel.update(
                    binding.editTextLogin.text.toString(),
                    binding.editTextNombre.text.toString(),
                    binding.editTextApellido.text.toString(),
                    binding.editTextContraseA.text.toString(),
                    binding.editTextTelefono1.text.toString().toInt(),
                    binding.editTextDNI.text.toString(),
                    binding.editTextDirecciN.text.toString()

                )
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                intent = Intent(this, ChatListActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show()
                binding.editTextContraseA.setText(" ")
                binding.editTextRepetirContraseA.setText(" ")
            }

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