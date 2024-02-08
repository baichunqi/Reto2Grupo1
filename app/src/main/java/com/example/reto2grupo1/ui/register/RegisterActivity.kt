package com.example.reto2grupo1.ui.register

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer

import com.example.reto2grupo1.data.Rol

import com.example.reto2grupo1.R

import com.example.reto2grupo1.data.repository.local.RoomUserDataSource
import com.example.reto2grupo1.data.repository.remote.RemoteAuthenticationRepository
import com.example.reto2grupo1.data.repository.remote.RemoteUserDataSource
import com.example.reto2grupo1.databinding.ActivityRegisterBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.utils.Resource
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale


class RegisterActivity : ComponentActivity() {
    private val authenticationRepository = RemoteAuthenticationRepository();
    private val userRepository = RemoteUserDataSource();
    private val localUserRepository = RoomUserDataSource();
    private var imageByteArray: ByteArray? = null
    private var imageBase64: String? = null
    private var imageBitmap: Bitmap? = null
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


    private val viewModel: RegisterViewModel by viewModels {
        RegisterActivityViewModelFactory(
            userRepository,authenticationRepository
        )
    }
    lateinit var roles :List<Rol>
    private var selectedImage: File? = null

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permiso de cámara concedido
                // Aquí puedes realizar otras acciones que deban ocurrir después de obtener el permiso
            } else {
                // Permiso de cámara denegado
                // Puedes mostrar un mensaje o tomar medidas apropiadas
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso no concedido, solicitarlo
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        } else {
            // Permiso ya concedido
            // Aquí puedes realizar otras acciones que deban ocurrir después de obtener el permiso
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

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
                        roles = data.roles
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
            Log.i("IntentExtra", intent.extras.toString())
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
            binding.imageView4.isVisible = true
            binding.buttonCambioDeContraseA.isVisible = false

            binding.imageView4.setOnClickListener() {
                Toast.makeText(this, "@string/revisarCampos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCambiarFoto.setOnClickListener {
            Log.i("pruebas", "entr?")
            takePictureLauncher.launch(takePictureIntent)
        }

        binding.imageView14?.setOnClickListener(){
            showPopup(it)
        }

        binding.buttonCambioDeContraseA.setOnClickListener {
            if (binding.editTextContraseA.text.toString() == binding.editTextRepetirContraseA.text.toString()) {
                if (binding.editTextContraseA.text.toString() != "Elorrieta00") {
                    viewModel.update(
                        binding.editTextLogin.text.toString(),
                        binding.editTextNombre.text.toString(),
                        binding.editTextApellido.text.toString(),
                        binding.editTextContraseA.text.toString(),
                        binding.editTextTelefono1.text.toString().toInt(),
                        binding.editTextDNI.text.toString(),
                        binding.editTextDirecciN.text.toString(),
                        roles

                    )
                } else {
                    Toast.makeText(this, "La contraseña no puede ser igual a la de por defecto", Toast.LENGTH_SHORT).show()
                }
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
        viewModel.imageBase64.observe(this, Observer { newImageBase64 ->
            Log.d(TAG, "ImageBase64 actualizado: $newImageBase64")
            //viewModel.onSendMessage(newImageBase64, intent.getStringExtra("id").toString())
            binding.imageView5.setImageBitmap(imageBitmap)
        })
    }

    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageBitmap = result.data?.extras?.get("data") as Bitmap?
                imageBitmap?.let {
                    imageByteArray = convertBitmapToByteArray(it)
                    imageBase64 = convertByteArrayToBase64(imageByteArray!!)
                    Log.d(TAG, "Contenido de imageBase64: $imageBase64")

                    // Actualiza la variable imageBase64 en el ViewModel
                    viewModel.updateImageBase64(imageBase64!!)
                }
            } else {
                Log.d(TAG, "Error al capturar la foto.")
            }
        }
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun convertByteArrayToBase64(byteArray: ByteArray) : String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File.createTempFile("image", ".jpg", cacheDir)
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()

        return file
    }

    fun showPopup(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_languages, popup.menu)
        popup.setOnMenuItemClickListener() { menuItem ->
            when(menuItem.itemId){
                R.id.es-> {
                    setLocale(this, "es")
                }
                R.id.eng-> {
                    setLocale(this, "en")
                }
                R.id.eus-> {
                    setLocale(this, "eu")
                }
            }
            true
        }
        popup.show()
    }

    private fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.recreate()
        } else {
            val intent = activity.intent
            activity.finish()
            activity.startActivity(intent)
        }

    }

}