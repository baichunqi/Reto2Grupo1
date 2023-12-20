package com.example.reto2grupo1.ui.chatList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.databinding.ItemChatListBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class ChatListAdapter(

): ListAdapter<Chat, ChatListAdapter.ChatListViewHolder>(ChatListDiffCallback()) {

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
//            binding.ImageChat.setImageBitmap()
            binding.textViewNombreChat.text = chat.nombreChat
            binding.textViewUltimoMensaje.text = chat.ultimoMensaje
    // Con esto cargamos la imagen desde una URL que puede ser el servidor de Laravel
//            val thumbnailUrl = song.imagen
//            Log.i("Prueba", "" + thumbnailUrl)
//            Picasso
//                .get()
//                .load(thumbnailUrl)
//                .into(binding.ImageChat)
        }
    }

    class ChatListDiffCallback: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem : Chat, newItem: Chat): Boolean {
            return oldItem.nombreChat == newItem.nombreChat
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }
}