package com.example.bluechat.presenter.activities

import android.bluetooth.BluetoothAdapter
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
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
import com.example.bluechat.presenter.viewmodels.factories.StartChatViewModelFactory
import com.example.bluechat.usecase.StartChatActivityUseCase
import com.example.bluechat.utils.Constants
import kotlinx.android.synthetic.main.activity_start_chat.*

class StartChatActivity : AppCompatActivity() {

    var bound = false

    val BT_REQUEST_CODE = 100
    lateinit var binder : MainBluetoothService.MainBluetoothBinder
    lateinit var viewModel : StartChatViewModel
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var visibility : BluetoothVisibility = BluetoothVisibility.Invisible()
    var scanState : BluetoothScanState = BluetoothScanState.NotScanning()

    val servConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = (service as MainBluetoothService.MainBluetoothBinder)
            val adapter = recyclerview_start_chat.adapter as AvailableDevicesRecyclerAdapter
            val useCase = StartChatActivityUseCase(
                StartChatRepository(binder.service)
            )
            viewModel = ViewModelProvider(this@StartChatActivity , StartChatViewModelFactory(useCase)).get(StartChatViewModel::class.java)

            viewModel.getAvailableDevices().observe(this@StartChatActivity, Observer{
                adapter.submitList(it)
            })
            viewModel.getVisibility().observe(this@StartChatActivity, Observer{
                this@StartChatActivity.visibility = it
                invalidateOptionsMenu()
            })
            viewModel.getScanState().observe(this@StartChatActivity, Observer{
                this@StartChatActivity.scanState = it
                invalidateOptionsMenu()
            })
            adapter.itemClickListener =
                object : AvailableDevicesRecyclerAdapter.OnItemClickListener {
                    override fun onItemClicked(address : String, deviceName : String?) {
                        useCase.openChatWithDevice(address) { success ->
                            Handler(Looper.getMainLooper()).post {
                                if(success) {
                                    with(Intent(this@StartChatActivity, ChatActivity::class.java)) {
                                        putExtra(Constants.INTENT_CHATPARTNER_ADDRESS, address)
                                        putExtra(Constants.INTENT_CHATPARTNER_NAME, deviceName)
                                        startActivity(this)
                                    }
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


        bindService(Intent(this, MainBluetoothService::class.java), servConn, Context.BIND_AUTO_CREATE)
        val adapter = AvailableDevicesRecyclerAdapter()
        recyclerview_start_chat.adapter = adapter
        recyclerview_start_chat.layoutManager = LinearLayoutManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(bound) unbindService(servConn)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_start_chat_activity, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val scanStateMenuItem = menu?.findItem(R.id.menuitem_startchat_scan)
        scanStateMenuItem?.title = when(scanState) {
            is BluetoothScanState.Scanning -> {
                "Scanning"
            }
            is BluetoothScanState.NotScanning -> {
                "Start Scan"
            }
        }

        val visibilityMenuItem = menu?.findItem(R.id.menuitem_startchat_visibility)
        visibilityMenuItem?.icon  = when(visibility) {
            is BluetoothVisibility.Visible -> {
                getDrawable(R.drawable.ic_visibility_black_24dp)
            }
            is BluetoothVisibility.Invisible -> {
                getDrawable(R.drawable.ic_visibility_off_black_24dp)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menuitem_startchat_scan -> {
                when(scanState) {
                    is BluetoothScanState.Scanning -> {
                        bluetoothAdapter?.cancelDiscovery()
                    }
                    is BluetoothScanState.NotScanning -> {
                        bluetoothAdapter?.cancelDiscovery()
                        bluetoothAdapter?.startDiscovery()
                    }
                }
                return true
            }
            R.id.menuitem_startchat_visibility -> {
                when(visibility) {
                    is BluetoothVisibility.Invisible -> {
                        enableBtDiscoverability()
                    }
                }
                return true
            }
            else -> {
                return false
            }
        }
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
