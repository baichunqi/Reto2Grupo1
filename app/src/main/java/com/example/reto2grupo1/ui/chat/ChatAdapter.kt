package com.example.reto2grupo1.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.databinding.ItemChatObjectRecieveBinding

class ChatAdapter(chatId: String?) : ListAdapter<Message, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder{
         val binding = ItemChatObjectRecieveBinding.inflate(LayoutInflater.from(parent.context),parent, false)

//        when (viewType) {
//            0 -> {
//                val binding = ItemChatObjectSendBinding.inflate(LayoutInflater.from(parent.context),parent, false)
//                return ChatViewHolder(binding)
//            }
//            1 -> {
//                val binding = ItemChatObjectRecieveBinding.inflate(LayoutInflater.from(parent.context),parent, false)
//                return ChatViewHolder(binding)
//            }
//        }


//         val binding = ItemChatObjectRecieveBinding.inflate(LayoutInflater.from(parent.context),parent, false)



//        val binding = ItemChatObjectSendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

//    override fun getItemViewType() {
//
//    }
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int){
        val message = getItem(position)
        holder.bind(message)
    }
    inner class ChatViewHolder(private val binding: ItemChatObjectRecieveBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
//            binding.ImageViewFoto.setImageBitmap()
            Log.i("Mensaje", message.toString())
            binding.TextViewMensaje.text = message.text
            binding.textViewTiempo.text = message.userId.toString()

        }
    }
    class ChatDiffCallback: DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}