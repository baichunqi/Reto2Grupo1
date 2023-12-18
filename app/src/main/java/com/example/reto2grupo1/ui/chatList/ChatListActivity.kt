package com.example.reto2grupo1.ui.chatList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.reto2grupo1.R
import com.example.reto2grupo1.databinding.ActivityChatListBinding
import com.example.reto2grupo1.ui.register.RegisterActivity


class ChatListActivity  : ComponentActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imageView7.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.imageView8.setOnClickListener() {
            showPopup(it)
        }
    }

    fun showPopup(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.settings-> {
                    Toast.makeText(this, "Ajustes", Toast.LENGTH_SHORT).show()
                }
                R.id.help-> {
                    Toast.makeText(this, "Ayuda", Toast.LENGTH_SHORT).show()
                }
                R.id.aboutUs-> {
                    Toast.makeText(this, "Sobre nosotros", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popup.show()
    }
}