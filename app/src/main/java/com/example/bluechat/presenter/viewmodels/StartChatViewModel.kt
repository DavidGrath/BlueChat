package com.example.bluechat.presenter.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bluechat.usecase.StartChatActivityUseCase

class StartChatViewModel(var useCase: StartChatActivityUseCase) : ViewModel(){
    fun getVisibility() = useCase.getVisibility()
    fun getAvailableDevices() = useCase.getAvailableDevices()
    fun getScanState() = useCase.getScanState()
}