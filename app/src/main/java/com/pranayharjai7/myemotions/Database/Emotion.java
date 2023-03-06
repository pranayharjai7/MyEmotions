package com.pranayharjai7.myemotions.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Emotion")
public class Emotion {

    @PrimaryKey(autoGenerate = true)
    private int key;
    private String userId;
    private String dateTime;
    private String emotion;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
