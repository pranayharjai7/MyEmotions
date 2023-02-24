package com.pranayharjai7.myemotions.ViewModels;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<View> view = new MutableLiveData<>();

    public LiveData<View> getView() {
        return view;
    }

    public void setView(View view) {
        this.view.setValue(view);
    }
}
