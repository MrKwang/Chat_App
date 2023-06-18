package com.example.myapplication.utilities

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.model.ChatMessage

class ChatDiffUtil(
    private val oldList: List<ChatMessage>,
    private val newList: List<ChatMessage>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMess = oldList[oldItemPosition]
        val newMess = newList[newItemPosition]
        return when{
             oldMess.message != newMess.message -> false
             oldMess.day != newMess.day -> false
             oldMess.time != newMess.time -> false
             oldMess.showAva != newMess.showAva -> false

             else -> true
         }


    }

}