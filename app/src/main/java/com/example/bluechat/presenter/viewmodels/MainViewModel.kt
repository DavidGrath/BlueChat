package com.example.bluechat.presenter.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bluechat.data.DataProvider
import com.example.bluechat.data.DataProviderImpl
import com.example.bluechat.data.repositories.MainRepository
import com.example.bluechat.usecase.MainActivityUseCase

class MainViewModel(var useCase: MainActivityUseCase) : ViewModel(){
    fun attemptConnectionWithDevice(address : String, onFinished : (success : Boolean) -> Unit) {
        useCase.attemptChatWithDevice(address, onFinished)
    }
    val dataProvider : DataProvider = DataProviderImpl
    val conversations = dataProvider.getConversations()
}