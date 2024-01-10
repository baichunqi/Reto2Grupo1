package com.example.reto2grupo1.ui.register

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.reto2grupo1.databinding.ActivityRegisterBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity

class RegisterActivity : ComponentActivity() {

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
                    val imageBitmap = it.data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        binding.imageView5.setImageBitmap(imageBitmap)
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
        }

    }


}