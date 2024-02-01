package com.example.reto2grupo1.ui.chat

import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.databinding.ItemChatObjectRecieveBinding
import java.nio.charset.StandardCharsets

class ChatAdapter(chatId: String?) : ListAdapter<Message, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {
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
    fun isBase64(input: String): Boolean {
        return try {
            val decodedBytes = Base64.decode(input, Base64.DEFAULT)
            String(decodedBytes, StandardCharsets.UTF_8)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    // Función para intentar analizar las coordenadas
    fun parseCoordinates(input: String): Pair<Double, Double>? {
        val coordinates = input.split(" ")
        if (coordinates.size == 2) {
            try {
                val latitude = coordinates[0].toDouble()
                val longitude = coordinates[1].toDouble()
                return Pair(latitude, longitude)
            } catch (e: NumberFormatException) {
                // Manejar el error si las coordenadas no son números válidos
            }
        }
        return null
    }
}