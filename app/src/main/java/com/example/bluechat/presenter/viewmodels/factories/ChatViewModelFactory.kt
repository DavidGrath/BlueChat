package com.example.bluechat.presenter.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bluechat.presenter.viewmodels.ChatViewModel
import com.example.bluechat.usecase.ChatActivityUseCase

class ChatViewModelFactory(var partnerAddress : String,var useCase: ChatActivityUseCase) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(partnerAddress, useCase) as T
    }
}