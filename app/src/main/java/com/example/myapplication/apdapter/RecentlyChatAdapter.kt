package com.example.myapplication.apdapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ContainerRecentUserItemBinding
import com.example.myapplication.model.RecentlyChatUser
import com.example.myapplication.utilities.RecentChatDiff


class RecentlyChatAdapter(private var list: MutableList<RecentlyChatUser>):
    RecyclerView.Adapter<RecentlyChatAdapter.CustomViewHolder>() {


    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ContainerRecentUserItemBinding.bind(view)
        fun setData(recentlyChatUser: RecentlyChatUser){
            binding.tvMessage.text = recentlyChatUser.lastMess
            binding.imgUser.setImageBitmap(setBitmap(recentlyChatUser.image))
            binding.tvUsername.text = recentlyChatUser.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.container_recent_user_item,parent,false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.setData(list[position])
    }
    private fun setBitmap(img: String): Bitmap{
        val decode = Base64.decode(img, Base64.DEFAULT )
        return BitmapFactory.decodeByteArray(decode,0,decode.size)
    }

    fun updateData(newList: List<RecentlyChatUser>){
        val diff = RecentChatDiff(list, newList)
        val calculate = DiffUtil.calculateDiff(diff)
        list.clear()
        list.addAll(newList)
        calculate.dispatchUpdatesTo(this)
    }
    fun getUpdatedList(): List<RecentlyChatUser>{
        return list
    }
}