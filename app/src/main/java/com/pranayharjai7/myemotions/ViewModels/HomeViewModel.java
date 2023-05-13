package com.pranayharjai7.myemotions.ViewModels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> emotionPic = new MutableLiveData<>();
    private final MutableLiveData<String> emotion = new MutableLiveData<>();

    public LiveData<Bitmap> getEmotionPic() {
        return emotionPic;
    }

    public void setEmotionPic(Bitmap emotionPic) {
        this.emotionPic.setValue(emotionPic);
    }

    public LiveData<String> getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion.setValue(emotion);
    }
}
