package com.example.reto2grupo1.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.databinding.ItemChatObjectRecieveBinding

class ChatAdapter(

): ListAdapter<Message, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder{
        val binding = ItemChatObjectRecieveBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ChatViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int){
        val message = getItem(position)
        holder.bind(message)
    }
    inner class ChatViewHolder(private val binding: ItemChatObjectRecieveBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
//            binding.ImageViewFoto.setImageBitmap()
            binding.TextViewMensaje.text = message.messageContent
            binding.textViewTiempo.text = message.date.toString()
        }
    }
    class ChatDiffCallback: DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageContent == newItem.messageContent
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}