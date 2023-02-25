package com.pranayharjai7.myemotions.ViewModels;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<View> loginActivityView = new MutableLiveData<>();
    private MutableLiveData<View> registerUserFragmentView = new MutableLiveData<>();

    public LiveData<View> getLoginActivityView() {
        return loginActivityView;
    }

    public void setLoginActivityView(View loginActivityView) {
        this.loginActivityView.setValue(loginActivityView);
    }

    public LiveData<View> getRegisterUserFragmentView() {
        return registerUserFragmentView;
    }

    public void setRegisterUserFragmentView(View registerUserFragmentView) {
        this.registerUserFragmentView.setValue(registerUserFragmentView);
    }
}
