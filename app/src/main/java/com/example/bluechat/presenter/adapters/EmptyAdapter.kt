package com.example.bluechat.presenter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bluechat.R

class EmptyAdapter(private val title : String,private val description : String) : RecyclerView.Adapter<EmptyAdapter.EmptyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_empty, parent, false)
        return EmptyViewHolder(v)
    }

    override fun onBindViewHolder(holder: EmptyViewHolder, position: Int) {
        with(holder) {
            emptyTitle.text = title
            emptyDescription.text = description
        }
    }

    override fun getItemCount(): Int = 1

    class EmptyViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        val emptyTitle = item.findViewById<TextView>(R.id.empty_title)
        val emptyDescription = item.findViewById<TextView>(R.id.empty_description)
    }
}