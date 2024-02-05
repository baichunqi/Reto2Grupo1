package com.example.reto2grupo1.ui.showUsers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.databinding.ItemUserBinding

class ShowUserAdapter(private val context: Context)
    : ListAdapter<User, ShowUserAdapter.ShowUserViewHolder>(ShowUserDiffCallback()){


    private var userListFull: List<User> = emptyList()

    fun submitChatList(users: List<User>?) {
        if (users != null) {
            userListFull = users
        }
    }

    inner class ShowUserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(user : User){

            binding.txtAddUser.text = user.name
            binding.textView5.text = user.email

    }
    }




    class ShowUserDiffCallback: DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem : User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowUserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowUserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }



}