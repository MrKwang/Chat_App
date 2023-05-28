package com.example.myapplication.apdapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.interfaces.OnItemCLickListener
import com.example.myapplication.R
import com.example.myapplication.databinding.ContainerUserItemBinding
import com.example.myapplication.model.UserList

class UserListAdapter(private val list: List<UserList>, private val mItemClick: OnItemCLickListener)
    : RecyclerView.Adapter<UserListAdapter.ViewHolderCustom>() {


    inner class ViewHolderCustom(item: View): RecyclerView.ViewHolder(item){

        private val binding: ContainerUserItemBinding = ContainerUserItemBinding.bind(item)
        fun setUserData(user: UserList){
            binding.imgUser.setImageBitmap(getBitMap(user.image))
            binding.tvUsername.text = user.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCustom {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.container_user_item,parent,false)

        return ViewHolderCustom(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolderCustom, position: Int) {
        holder.setUserData(list[position])
        holder.itemView.setOnClickListener {
            mItemClick.onClick(position)
        }
    }

    private fun getBitMap( img: String): Bitmap{
        val bytes = Base64.decode(img, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}