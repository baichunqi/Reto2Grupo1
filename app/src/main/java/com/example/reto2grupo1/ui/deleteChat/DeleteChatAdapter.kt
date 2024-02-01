package com.example.reto2grupo1.ui.deleteChat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Chat
import com.example.reto2grupo1.databinding.ItemChatDeleteBinding

class DeleteChatAdapter (private val onDeleteClickListener: (Chat) -> Unit) :ListAdapter<Chat, DeleteChatAdapter.DeleteChatViewHolder>(DeleteChatDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteChatAdapter.DeleteChatViewHolder {
        val binding = ItemChatDeleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeleteChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeleteChatViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat, onDeleteClickListener)
    }

    inner class DeleteChatViewHolder(private val binding: ItemChatDeleteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(chat: Chat, onDeleteClickListener: (Chat) -> Unit){
            binding.chatName.text = chat.name
            binding.deleteChat.setOnClickListener{
                onDeleteClickListener(chat)
            }
        }
    }
}

 class DeleteChatDiffCallback : DiffUtil.ItemCallback<Chat>(){

     override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
         return oldItem.name == newItem.name
     }

     override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
         return oldItem == newItem
     }
 }