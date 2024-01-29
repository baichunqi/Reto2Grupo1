package com.example.reto2grupo1.ui.createGroup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.reto2grupo1.data.repository.remote.RemoteCreateChatDataSource
import com.example.reto2grupo1.databinding.ActivityCreateChatBinding

class CreateGroupActivity : ComponentActivity()  {

    private val createGroupRepository = RemoteCreateChatDataSource()

    private val viewModel: CreateGroupViewModel by viewModels {CreateGroupViewModelFactory(createGroupRepository)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCreateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreate.setOnClickListener(){
            val name = binding.editTextName.text.toString()
            var private = false
            if (binding.switchPrivate.isChecked)
                private = true
            // TODO falta especificar los datos que hay que pasarle
            viewModel.createChat(name, private)
            finish()
        }
    }
}