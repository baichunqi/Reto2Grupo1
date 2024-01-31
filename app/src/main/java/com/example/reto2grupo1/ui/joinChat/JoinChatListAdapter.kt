package com.example.reto2grupo1.ui.joinChat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.databinding.ItemChatListBinding
import com.example.reto2grupo1.ui.chatList.ChatListActivity
import com.example.reto2grupo1.ui.chatList.ChatListAdapter
import java.util.Locale

class JoinChatListAdapter(private val context: Context)
    : ListAdapter<Chat, JoinChatListAdapter.JoinChatListViewHolder>(JoinChatListAdapter.JoinChatListDiffCallback()) {


    private var chatListFull: List<Chat> = emptyList()
    private var chatListFiltered: List<Chat> = emptyList()



    // Método para establecer la lista completa de canciones y actualizar la lista filtrada
    fun submitChatList(chats: List<Chat>?) {
        if (chats != null) {
            chatListFull = chats
        }
        filter("",  true) // Al recibir una nueva lista de canciones, mostramos todas las canciones
    }
    fun filter(text: String, esPublico : Boolean) {
        val searchText = text.trim().lowercase(Locale.getDefault())

        chatListFiltered = if (searchText.isEmpty()) {
            chatListFull // Restauramos la lista completa si el texto está vacío
        } else {
            chatListFull.filter {
                if(esPublico){
                    it.name.lowercase(Locale.getDefault()).contains(searchText).and(!it.private)

                } else {
                    it.name.lowercase(Locale.getDefault()).contains(searchText).and( it.private)

                }
                // Puedes agregar otros campos aquí para el filtrado
            }
        }

        submitList(chatListFiltered) // Mostramos los resultados filtrados en el RecyclerView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinChatListViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JoinChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JoinChatListViewHolder, position: Int){
        val chat = getItem(position)
        holder.bind(chat)

    }

    inner class JoinChatListViewHolder(private val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(chat : Chat){
            binding.txtAddUser.text = chat.name
            binding.buttonJoin.isVisible = true
            // Llamada a la función selectChat al hacer clic en un elemento
            binding.buttonJoin.setOnClickListener {
                Log.d("estoentra","estoentra");
                (context as JoinChatActivity)joinChat(chat)

            }
        }
    }

    class JoinChatListDiffCallback: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem : Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    }