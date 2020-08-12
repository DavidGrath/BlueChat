package com.example.bluechat.presenter.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bluechat.R
import com.example.bluechat.domain.models.Conversation

class ConversationRecyclerAdapter(private var conversations : ArrayList<Conversation>) : RecyclerView.Adapter<ConversationRecyclerAdapter.ConversationViewHolder>(){

    interface ItemClickListener{
        fun onItemClicked(address : String, name : String?)
    }
    var itemClickListener : ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_conversation, parent, false)
        return ConversationViewHolder(v)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val convo : Conversation = conversations[position]
        val dateFormat = DateFormat.getTimeFormat(holder.itemView.context)
        with(holder) {
            conversationName.text = convo.user.name?: convo.user.address
            conversationLastMessage.text = convo.lastChat.content
            conversationLastMessageTime.text = dateFormat.format(convo.lastChat.timestamp)
            conversationUnreadCount.text = convo.unreadCount.toString()
            itemView.setOnClickListener{
                itemClickListener?.onItemClicked(convo.user.address, convo.user.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    fun submitList(conversations: ArrayList<Conversation>) {
        this.conversations = conversations
        notifyDataSetChanged()
    }

    class ConversationViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        val conversationName = item.findViewById<TextView>(R.id.conversation_name)
        val conversationLastMessage = item.findViewById<TextView>(R.id.conversation_latest_message)
        val conversationLastMessageTime = item.findViewById<TextView>(R.id.conversation_latest_time)
        val conversationUnreadCount = item.findViewById<TextView>(R.id.conversation_unread_count)
    }
}