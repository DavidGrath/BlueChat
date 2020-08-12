package com.example.bluechat.usecase

import com.example.bluechat.data.repositories.MainRepository

public class MainActivityUseCase(var repository: MainRepository) : UseCase {
    fun attemptChatWithDevice(address : String, onFinished : (success : Boolean)->Unit) {
        repository.attemptConnectionWithDevice(address, onFinished)
    }
}
