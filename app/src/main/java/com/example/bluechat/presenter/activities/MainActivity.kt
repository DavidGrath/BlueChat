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
import com.example.bluechat.presenter.adapters.ConversationRecyclerAdapter
import com.example.bluechat.domain.models.Conversation
import com.example.bluechat.framework.services.MainBluetoothService
import com.example.bluechat.presenter.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener{

    val BT_REQUEST_CODE = 100
    val ACCESS_COARSE_REQUEST_CODE = 200
//    var bound = false
//    lateinit var binder : MainBluetoothService.MainBluetoothBinder

    lateinit var viewModel : MainViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        checkPermissions()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val adapter = ConversationRecyclerAdapter(ArrayList<Conversation>())
        recyclerview_main_conversations.adapter = adapter
        recyclerview_main_conversations.layoutManager = LinearLayoutManager(this)
        viewModel.conversations.observe(this, Observer {
            adapter.submitList(it)
        })
        startService(Intent(this, MainBluetoothService::class.java))

        fab_main.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
//        if(bound) unbindService(servConn)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.main_menu_addchat -> {
                return true
            }
            else -> {
                return false
            }
        }
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
