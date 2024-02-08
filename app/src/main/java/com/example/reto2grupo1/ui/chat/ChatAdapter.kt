package com.example.reto2grupo1.ui.chat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.Message
import com.example.reto2grupo1.databinding.ItemChatObjectRecieveBinding

class ChatAdapter() : ListAdapter<Message, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder{
         val binding = ItemChatObjectRecieveBinding.inflate(LayoutInflater.from(parent.context),parent, false)

        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int){
        val message = getItem(position)
        holder.bind(message)

        // Verificar si el mensaje es un par de coordenadas
        if (isCoordenate(message.text)) {
            // Abrir Google Maps al hacer clic en el mensaje
            holder.itemView.setOnClickListener {
                abrirGoogleMaps(message.text, holder.itemView)
            }
        }
    }
    inner class ChatViewHolder(private val binding: ItemChatObjectRecieveBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
            val date = message.created_at.substring(0, 16).replace("T", " ")
            if(isBase64(message.text)){
                var imagen = base64ToBitmap(message.text)
                binding.ImageViewImage.setImageBitmap(imagen)
                binding.ImageViewImage.visibility = View.VISIBLE
                binding.TextViewMensaje.visibility = View.GONE
                Log.i("UserId", message.userId)
                binding.textViewTiempo.text = message.userId
                binding.textViewCreatedAt.text = date
            }
            else if(isCoordenate(message.text)){
                binding.TextViewMensaje.visibility = View.VISIBLE
                binding.ImageViewImage.visibility = View.GONE
                val spannableString = SpannableString(message.text)
                spannableString.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                    }
                }, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#0000FF")),
                    0, spannableString.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.TextViewMensaje.text = spannableString
                binding.textViewTiempo.text = message.userId
                binding.textViewCreatedAt.text = date
            } else{
                binding.TextViewMensaje.visibility = View.VISIBLE
                binding.ImageViewImage.visibility = View.GONE
                binding.TextViewMensaje.text = message.text
                binding.textViewTiempo.text = message.userId
                binding.textViewCreatedAt.text = date
            }
        }
    }
fun isBase64(cadena: String): Boolean {
    if (cadena.contains(" ") || cadena.length <= 100) {
        return false
    }
    return true
}
    private fun isCoordenate(cadena: String): Boolean {
        val partes = cadena.split(" ")

        if (partes.size != 2) {
            return false
        }

        return try {
            val latitud = partes[0].toDouble()
            val longitud = partes[1].toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
    private fun abrirGoogleMaps(coordenadas: String, itemView: View) {
        val context = itemView.context
        val coordenadasConComas = coordenadas.replace(" ", ",")
//        val uri = Uri.parse("geo:${coordenadasConComas}?q=")
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        intent.setPackage("com.google.android.apps.maps")
        val intent = Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?saddr=${coordenadasConComas}&daddr=42.4219983,-122.084"))
        context.startActivity(intent)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
    fun base64ToBitmap(base64String: String): Bitmap? {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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
