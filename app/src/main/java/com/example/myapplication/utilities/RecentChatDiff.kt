package com.example.myapplication.utilities

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.model.RecentlyChatUser

class RecentChatDiff(
    private val oldList: List<RecentlyChatUser>,
    private val newList: List<RecentlyChatUser>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMess = oldList[oldItemPosition]
        val newMess = newList[newItemPosition]
        return when{
             oldMess.lastMess != newMess.lastMess -> false
             oldMess.time != newMess.time -> false
             oldMess.image!= newMess.image -> false
             oldMess.username != newMess.username -> false
             else -> true
         }


    }

}