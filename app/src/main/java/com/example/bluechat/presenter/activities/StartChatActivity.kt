package com.example.bluechat.presenter.activities

import android.bluetooth.BluetoothAdapter
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluechat.R
import com.example.bluechat.data.BluetoothScanState
import com.example.bluechat.data.BluetoothVisibility
import com.example.bluechat.data.repositories.StartChatRepository
import com.example.bluechat.presenter.adapters.AvailableDevicesRecyclerAdapter
import com.example.bluechat.framework.services.MainBluetoothService
import com.example.bluechat.presenter.viewmodels.StartChatViewModel
import com.example.bluechat.usecase.StartChatActivityUseCase
import kotlinx.android.synthetic.main.activity_start_chat.*

class StartChatActivity : AppCompatActivity() {

    var bound = false

    val BT_REQUEST_CODE = 100
    lateinit var binder : MainBluetoothService.MainBluetoothBinder
    lateinit var viewModel : StartChatViewModel
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var visibility = "Discoverable"
    var scanState = "Not Scanning"

    val servConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = (service as MainBluetoothService.MainBluetoothBinder)
            val adapter = recyclerview_start_chat.adapter as AvailableDevicesRecyclerAdapter
            val useCase = StartChatActivityUseCase(
                StartChatRepository(binder.service)
            )
            useCase.getAvailableDevices().observe(this@StartChatActivity, Observer{
                adapter.submitList(it)
            })
            useCase.getVisibility().observe(this@StartChatActivity, Observer{
                when(it) {
                    is BluetoothVisibility.Visible -> {
                        visibility = "Discoverable"
                    }
                    is BluetoothVisibility.Invisible -> {
                        visibility = "Not Discoverable"
                    }
                }
                invalidateOptionsMenu()
            })
            useCase.getScanState().observe(this@StartChatActivity, Observer{
                when(it) {
                    is BluetoothScanState.Scanning -> {
                        scanState = "Scanning"
                    }
                    is BluetoothScanState.NotScanning -> {
                        scanState = "Not Scanning"
                    }
                }
                invalidateOptionsMenu()
            })
            adapter.itemClickListener =
                object : AvailableDevicesRecyclerAdapter.OnItemClickListener {
                    override fun onItemClicked(position: Int) {
                        useCase.openChatWithDevice(position) { success ->
                            Handler(Looper.getMainLooper()).post {
                                if(success) {
                                    startActivity(Intent(this@StartChatActivity, ChatActivity::class.java))
                                } else {
                                    Toast.makeText(this@StartChatActivity, "Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_chat)
        setSupportActionBar(toolbar_start_chat)
        title = "Start a Chat"

        viewModel = ViewModelProvider(this).get(StartChatViewModel::class.java)
        bindService(Intent(this, MainBluetoothService::class.java), servConn, Context.BIND_AUTO_CREATE)
        val adapter = AvailableDevicesRecyclerAdapter()
        recyclerview_start_chat.adapter = adapter
        recyclerview_start_chat.layoutManager = LinearLayoutManager(this)
        bluetoothAdapter?.startDiscovery()
        enableBtDiscoverability()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(bound) unbindService(servConn)
    }

    fun startBluetoothScan() {
        bluetoothAdapter?.cancelDiscovery()
        bluetoothAdapter?.startDiscovery()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_start_chat_activity, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val scanStateMenuItem = menu?.findItem(R.id.menuitem_startchat_scanstate)
        scanStateMenuItem?.title = scanState

        val visibilityMenuItem = menu?.findItem(R.id.menuitem_startchat_visibility)
        visibilityMenuItem?.title = visibility
        return super.onPrepareOptionsMenu(menu)
    }

    fun enableBtDiscoverability() {
        if(bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, BT_REQUEST_CODE)
        }

        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(discoverableIntent)
    }
}
