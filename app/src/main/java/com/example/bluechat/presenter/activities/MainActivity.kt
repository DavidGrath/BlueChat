package com.example.bluechat.presenter.activities

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluechat.R
import com.example.bluechat.data.repositories.MainRepository
import com.example.bluechat.presenter.adapters.ConversationRecyclerAdapter
import com.example.bluechat.domain.models.Conversation
import com.example.bluechat.framework.services.MainBluetoothService
import com.example.bluechat.presenter.adapters.EmptyAdapter
import com.example.bluechat.presenter.viewmodels.MainViewModel
import com.example.bluechat.presenter.viewmodels.factories.MainViewModelFactory
import com.example.bluechat.usecase.MainActivityUseCase
import com.example.bluechat.utils.Constants.Companion.INTENT_CHATPARTNER_ADDRESS
import com.example.bluechat.utils.Constants.Companion.INTENT_CHATPARTNER_NAME
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener{

    val BT_REQUEST_CODE = 100
    val ACCESS_COARSE_REQUEST_CODE = 200
    var bound = false
    lateinit var binder : MainBluetoothService.MainBluetoothBinder

    lateinit var viewModel : MainViewModel
    val emptyAdapter = EmptyAdapter("No Chats", "Click the \"+\" icon to start a new chat")

    val servConn = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as MainBluetoothService.MainBluetoothBinder
            val repository = MainRepository(binder.service)
            val useCase = MainActivityUseCase(repository)
            viewModel = ViewModelProvider(this@MainActivity, MainViewModelFactory(useCase)).get(MainViewModel::class.java)
            val adapter = ConversationRecyclerAdapter(ArrayList<Conversation>())
            recyclerview_main_conversations.adapter = adapter
            recyclerview_main_conversations.layoutManager = LinearLayoutManager(this@MainActivity)
            viewModel.conversations.observe(this@MainActivity, Observer {
                if(it.isEmpty()) {
                    recyclerview_main_conversations.adapter = emptyAdapter
                } else {
                    recyclerview_main_conversations.adapter = adapter
                }
                adapter.submitList(it)
            })
            adapter.itemClickListener = object : ConversationRecyclerAdapter.ItemClickListener {
                override fun onItemClicked(address: String, name : String?) {
                    viewModel.attemptConnectionWithDevice(address) { success ->
                        Handler(Looper.getMainLooper()).post {
                            if(!success) {
                                Toast.makeText(this@MainActivity, "Connection Failed", Toast.LENGTH_SHORT).show()
                            }
                            with(Intent(this@MainActivity, ChatActivity::class.java)) {
                                putExtra(INTENT_CHATPARTNER_ADDRESS, address)
                                putExtra(INTENT_CHATPARTNER_NAME, name)
                                startActivity(this)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        checkPermissions()

        startService(Intent(this, MainBluetoothService::class.java))
        bindService(Intent(this, MainBluetoothService::class.java), servConn, Context.BIND_AUTO_CREATE)

        fab_main.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(bound) unbindService(servConn)
    }

    fun checkPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    or
                    (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE_REQUEST_CODE)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            BT_REQUEST_CODE -> {
                if(resultCode == RESULT_OK) {
                    Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let{
            when(it) {
                fab_main -> {
                    startActivity(Intent(this, StartChatActivity::class.java))
                }
            }
        }
    }

}
