package com.example.reto2grupo1.ui.createGroup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.data.repository.remote.RemoteCreateChatDataSource
import com.example.reto2grupo1.databinding.ActivityCreateChatBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.utils.Resource

class CreateGroupActivity : ComponentActivity()  {

    private val createGroupRepository = RemoteCreateChatDataSource()

    private val viewModel: CreateGroupViewModel by viewModels {CreateGroupViewModelFactory(createGroupRepository)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCreateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView6.setOnClickListener(){
            finish()
        }

        binding.btnCreate.setOnClickListener(){
            val name = binding.editTextName.text.toString()
            var private = false
            if (binding.switchPrivate.isChecked)
                private = true
            if(binding.editTextName.text.isNullOrEmpty()){
                Toast.makeText(this, "El grupo debe tener un nombre", Toast.LENGTH_SHORT).show()
            }else {
                val chat = Chat(null, name, "", private)
                viewModel.createChat(chat)
            }
        }
        viewModel.createChatResult.observe(this, Observer {
            when (it.status){

                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Grupo creado", Toast.LENGTH_SHORT).show()
                    finish()
                }
                Resource.Status.ERROR -> {
                    val errorMessage = it.message
                    if (errorMessage != null) {
                        if (errorMessage.contains("403")) {
                            Toast.makeText(this, "Compruebe sus privilegios", Toast.LENGTH_LONG)
                                .show()
                        } else if (errorMessage.contains("201")){
                            Toast.makeText(this, "Grupo creado", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ChatListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                Resource.Status.LOADING -> {

                }
            }
        })
    }
}