package com.example.myapplication.apdapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ContainerRecentUserItemBinding
import com.example.myapplication.model.ChatMessage
import android.util.Base64


class RecentlyChatAdapter(val list: List<ChatMessage>):
    RecyclerView.Adapter<RecentlyChatAdapter.CustomViewHolder>() {


    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ContainerRecentUserItemBinding.bind(view)
        fun setData(chatMessage: ChatMessage){
            binding.tvMessage.text = chatMessage.message
            binding.imgUser.setImageBitmap(setBitmap(chatMessage.))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
    private fun setBitmap(img: String): Bitmap{
        val decode = Base64.decode(img, Base64.DEFAULT )
        return BitmapFactory.decodeByteArray(decode,0,decode.size)
    }
}