package com.example.reto2grupo1.ui.AddUser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.databinding.ItemUserBinding

class AddUserAdapter()
    : ListAdapter<User, AddUserAdapter.AddUserViewHolder>(AddUserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int) : AddUserViewHolder{
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddUserViewHolder(binding)
    }
    override fun onBindViewHolder(holder: AddUserViewHolder, position : Int){
        val user = getItem(position)
        holder.bind(user)
    }
    inner class AddUserViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user : User){
            binding.txtAddUser.text = user.name + " " + user.surname
            binding.textView5.text = user.email
        }

    }
    class AddUserDiffCallback: DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
