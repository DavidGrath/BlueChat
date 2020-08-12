package com.example.bluechat.presenter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluechat.R
import com.example.bluechat.domain.models.AvailableDevice

class AvailableDevicesRecyclerAdapter() : ListAdapter<AvailableDevice, AvailableDevicesRecyclerAdapter.AvailableDevicesViewHolder>(callback){

    interface OnItemClickListener{
        fun onItemClicked(address: String, name : String?)
    }
    var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableDevicesViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.listview_bluetooth_item, parent, false)
        return AvailableDevicesViewHolder(v)
    }

    override fun onBindViewHolder(holder: AvailableDevicesViewHolder, position: Int) {
        val view = holder.itemView
        val device = getItem(position)

        with(holder) {
            btDeviceName.text = device.name?: "undefined"
            btDeviceAddress.text = device.address
//            btDeviceConnectingBar.visibility = if(deviceGeneric.state is SocketConnectionState.Connecting) {
//                View.VISIBLE
//            } else {
//                View.INVISIBLE
//            }
        }

        view.setOnClickListener{
            itemClickListener?.onItemClicked(device.address, device.name)
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<AvailableDevice>() {
            override fun areItemsTheSame(oldItem: AvailableDevice, newItem: AvailableDevice): Boolean {
                return oldItem.address == newItem.address
            }

            override fun areContentsTheSame(oldItem: AvailableDevice, newItem: AvailableDevice): Boolean {
                return true
            }

        }
    }
    class AvailableDevicesViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        val btDeviceName = item.findViewById<TextView>(R.id.label_bluetooth_item_name)
        val btDeviceAddress = item.findViewById<TextView>(R.id.label_bluetooth_item_mac)
//        val btDeviceConnectingBar = item.findViewById<ProgressBar>(R.id.progressBar)
    }
}