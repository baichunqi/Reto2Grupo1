package com.example.reto2grupo1.ui.chatList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.databinding.ItemChatListBinding
import java.util.Locale

class ChatListAdapter(private val context: Context)
    : ListAdapter<Chat, ChatListAdapter.ChatListViewHolder>(ChatListDiffCallback()) {

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
                    it.name.lowercase(Locale.getDefault()).contains(searchText).and(!it.is_private)

                } else {
                    it.name.lowercase(Locale.getDefault()).contains(searchText).and( it.is_private)

                }
                // Puedes agregar otros campos aquí para el filtrado
            }
        }

        submitList(chatListFiltered) // Mostramos los resultados filtrados en el RecyclerView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int){
        val chat = getItem(position)
        holder.bind(chat)

    }

    inner class ChatListViewHolder(private val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(chat : Chat){
            binding.textViewNombreChat.text = chat.name
            binding.buttonJoin.isVisible = false
            // Llamada a la función selectChat al hacer clic en un elemento
            binding.root.setOnClickListener {
                (context as ChatListActivity)selectChat(chat)
            }
        }
    }

    class ChatListDiffCallback: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem : Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }
}