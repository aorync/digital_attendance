package com.syntxr.digitalattendance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.syntxr.digitalattendance.R
import com.syntxr.digitalattendance.databinding.ItemUsersBinding
import com.syntxr.digitalattendance.data.model.users.UsersRensponse
import com.syntxr.digitalattendance.data.model.users.UsersRensponseItem

class UsersAdapter(val listUsers : UsersRensponse) :
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    var ItemClick : ((UsersRensponseItem) -> Unit)? = null
    var ItemDelete : ((UsersRensponseItem)-> Unit)? = null

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : ItemUsersBinding by viewBinding()

        fun bindView(usersRensponseItem: UsersRensponseItem){
            binding.tvItemUsername.text = usersRensponseItem.name
            binding.tvItemEmail.text = usersRensponseItem.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users,parent,false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val users = listUsers[position]
        holder.bindView(users)
        holder.binding.tvItemId.text = (position + 1).toString()
        holder.binding.btnDeleteUsers.setOnClickListener {
            ItemDelete?.invoke(users)
        }
        holder.binding.cardItemListUser.setOnClickListener {
            ItemClick?.invoke(users)
        }
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }
}