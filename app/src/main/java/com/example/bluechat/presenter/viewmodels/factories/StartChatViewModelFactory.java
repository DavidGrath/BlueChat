package com.example.bluechat.presenter.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.bluechat.presenter.viewmodels.StartChatViewModel;
import com.example.bluechat.usecase.StartChatActivityUseCase;

public class StartChatViewModelFactory implements ViewModelProvider.Factory {
    StartChatActivityUseCase useCase;
    public StartChatViewModelFactory(StartChatActivityUseCase useCase) {
        this.useCase = useCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new StartChatViewModel(useCase);
    }
}
