package com.example.bluechat.presenter.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bluechat.data.DataProvider
import com.example.bluechat.data.DataProviderImpl

class MainViewModel : ViewModel(){
    val dataProvider : DataProvider = DataProviderImpl
    val conversations = dataProvider.getConversations()
}