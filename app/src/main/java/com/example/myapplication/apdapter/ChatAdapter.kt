package com.example.myapplication.apdapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ReceivedMessageLayoutBinding
import com.example.myapplication.databinding.SentMessageLayoutBinding
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.utilities.ChatDiffUtil


class ChatAdapter(private val receivedBitmap: Bitmap, private var list: MutableList<ChatMessage>, private val sendId: String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    inner class SentMessageViewHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = SentMessageLayoutBinding.bind(item)
        fun setMessageSentData(position: Int){

            binding.tvSent.text = list[position].message
            binding.tvTime.text = list[position].time
            binding.tvDay.text = list[position].day

        }

    }

    inner class ReceivedMessageViewHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = ReceivedMessageLayoutBinding.bind(item)

        fun setMessageReceiveData( chatMessage: ChatMessage,image: Bitmap){

            binding.tvReceived.text = chatMessage.message
            binding.tvTime.text = chatMessage.time
            binding.imgUserChat.setImageBitmap(image)
            binding.tvDay.text = chatMessage.day

            if(chatMessage.showAva) binding.imgUserChat.visibility = View.VISIBLE else binding.imgUserChat.visibility = View.INVISIBLE
        }

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sent_message_layout, parent, false)
            )
        } else
            ReceivedMessageViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.received_message_layout, parent, false)
            )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val day = holder.itemView.findViewById<TextView>(R.id.tvDay)
        val prePosition = if(position > 0) position - 1 else null
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        when(getItemViewType(position)){
            VIEW_TYPE_RECEIVED -> (holder as ReceivedMessageViewHolder).setMessageReceiveData(list[position],receivedBitmap)
            VIEW_TYPE_SENT -> (holder as SentMessageViewHolder ).setMessageSentData(position)
        }

        if(prePosition != null ){
            if(getItemViewType(position) != getItemViewType(prePosition)  ){
                layoutParams.setMargins(0,dpToPx(20,holder.itemView),0,0)

            } else {
                val currentTime = list[position].timeStamp.time
                val preTime = list[prePosition].timeStamp.time
                val differentTime = (currentTime - preTime) / (1000 * 60)

                if (differentTime > 1) {
                    layoutParams.setMargins(0, dpToPx(5,holder.itemView), 0, 0)
                } else {
                    layoutParams.setMargins(0, 0, 0, 0)

                }
                holder.itemView.layoutParams = layoutParams

                if (list[position].day != list[prePosition].day ) {
                    day.visibility = View.VISIBLE
                } else
                    day.visibility = View.GONE
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(list[position].sendId == sendId){
            VIEW_TYPE_SENT
        } else
            VIEW_TYPE_RECEIVED
    }

    fun getData(newList: MutableList<ChatMessage>){
        val diff = ChatDiffUtil(list, newList)
        val diffResult = DiffUtil.calculateDiff(diff)
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    private fun dpToPx(dp: Int, view: View): Int{
        return dp * view.resources.displayMetrics.density.toInt()
    }
}