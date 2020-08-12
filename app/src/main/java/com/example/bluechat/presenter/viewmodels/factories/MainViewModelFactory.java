package com.example.bluechat.presenter.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.bluechat.presenter.viewmodels.MainViewModel;
import com.example.bluechat.usecase.MainActivityUseCase;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    MainActivityUseCase useCase;

    public MainViewModelFactory(MainActivityUseCase useCase) {
        this.useCase = useCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(useCase);
    }
}
