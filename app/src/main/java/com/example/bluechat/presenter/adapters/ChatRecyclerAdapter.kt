package com.example.bluechat.presenter.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bluechat.R
import com.example.bluechat.domain.models.Chat

class ChatRecyclerAdapter(var currentDeviceAddress : String, var items : ArrayList<Chat>) : RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder>() {

    companion object {
        val chatDiffUtil = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.content.equals(newItem.content)
                        && oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.content.equals(newItem.content)
                        && oldItem.timestamp == newItem.timestamp
            }
        }
    }

    fun getItem(position: Int) = items[position]
    fun submitList(items : ArrayList<Chat>){
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    val SENT_VIEW_TYPE = 100
    val RECEIVED_VIEW_TYPE = 200

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val viewId = if(viewType == SENT_VIEW_TYPE) {
            R.layout.chatbubble_send
        } else {
            R.layout.chatbubble_receive
        }
        val v = LayoutInflater.from(parent.context).inflate(viewId, parent, false)
        return if(viewType == SENT_VIEW_TYPE) {
            ChatSentViewHolder(v)
        } else {
            ChatReceivedViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        val dateFormat = DateFormat.getTimeFormat(holder.itemView.context)
        with(holder) {
            chatText.text = chat.content
            chatTime.text = dateFormat.format(chat.timestamp)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).senderAddress.equals(currentDeviceAddress)) {
            SENT_VIEW_TYPE
        } else {
            RECEIVED_VIEW_TYPE
        }
    }

    open class ChatViewHolder(var item : View) : RecyclerView.ViewHolder(item) {
        lateinit var chatText : TextView
        lateinit var chatTime : TextView
    }

    class ChatSentViewHolder(item : View) : ChatViewHolder(item) {
        init {
            chatText = item.findViewById(R.id.chatbubble_send_text)
            chatTime = item.findViewById(R.id.chatbubble_send_time)
        }
    }
    class ChatReceivedViewHolder(item : View) : ChatViewHolder(item) {
        init {
            chatText = item.findViewById(R.id.chatbubble_receive_text)
            chatTime = item.findViewById(R.id.chatbubble_receive_time)
        }
    }
}