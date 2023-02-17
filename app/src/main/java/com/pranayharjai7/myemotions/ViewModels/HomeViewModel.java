package com.pranayharjai7.myemotions.ViewModels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> emotionPic = new MutableLiveData<>();

    public LiveData<Bitmap> getEmotionPic() {
        return emotionPic;
    }

    public void setEmotionPic(Bitmap emotionPic) {
        this.emotionPic.setValue(emotionPic);
    }
}
