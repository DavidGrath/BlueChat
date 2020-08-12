package com.example.bluechat.presenter.activities

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluechat.R
import com.example.bluechat.data.DataProvider
import com.example.bluechat.data.DataProviderImpl
import com.example.bluechat.data.repositories.ChatRepository
import com.example.bluechat.presenter.adapters.ChatRecyclerAdapter
import com.example.bluechat.presenter.adapters.EmptyAdapter
import com.example.bluechat.domain.models.Chat
import com.example.bluechat.framework.services.MainBluetoothService
import com.example.bluechat.utils.SocketCallback
import com.example.bluechat.presenter.viewmodels.ChatViewModel
import com.example.bluechat.presenter.viewmodels.factories.ChatViewModelFactory
import com.example.bluechat.usecase.ChatActivityUseCase
import com.example.bluechat.utils.Constants.Companion.INTENT_CHATPARTNER_ADDRESS
import com.example.bluechat.utils.Constants.Companion.INTENT_CHATPARTNER_NAME
import com.example.bluechat.utils.Constants.Companion.OWN_ADDRESS
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer as LifecycleObserver

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binder : MainBluetoothService.MainBluetoothBinder
    lateinit var viewModel : ChatViewModel
    lateinit var address : String
    var deviceName : String? = null

    val servConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as MainBluetoothService.MainBluetoothBinder
            val dataProvider : DataProvider = DataProviderImpl
            val useCase = ChatActivityUseCase(
                ChatRepository(dataProvider, binder.service)
            )
            viewModel = ViewModelProvider(this@ChatActivity, ChatViewModelFactory(address, useCase)).get(ChatViewModel::class.java)
            viewModel.connectionTitleLiveData.observe(this@ChatActivity, LifecycleObserver {
                supportActionBar?.subtitle = it
            })

            Handler(Looper.getMainLooper()).post{
                title = deviceName?:address
                val adapter = ChatRecyclerAdapter(OWN_ADDRESS, ArrayList<Chat>())
                recyclerview_chat_main.adapter = adapter
                recyclerview_chat_main.layoutManager = LinearLayoutManager(this@ChatActivity, RecyclerView.VERTICAL, true)
                viewModel.chatsLiveData.observe(this@ChatActivity, LifecycleObserver{
                    adapter.submitList(it)
                })
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        address = intent.getStringExtra(INTENT_CHATPARTNER_ADDRESS)
        deviceName = intent.getStringExtra(INTENT_CHATPARTNER_NAME)

        imageview_send_chat.setOnClickListener(this)

        recyclerview_chat_main.adapter = EmptyAdapter("No Chats", "Meh")
        recyclerview_chat_main.layoutManager = LinearLayoutManager(this)
        bindService(Intent(this, MainBluetoothService::class.java), servConn, Context.BIND_AUTO_CREATE)
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it) {
                imageview_send_chat -> {
                    val text = edittext_chat_text.text.toString()
                    edittext_chat_text.setText("")
                    viewModel.sendMessage(text, address, deviceName)
                }
            }
        }
    }
}
