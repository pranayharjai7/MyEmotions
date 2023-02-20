package com.pranayharjai7.myemotions.Database.LocalDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Emotion")
public class Emotion {

    @PrimaryKey(autoGenerate = true)
    private int key;
    private String dateTime;
    private String emotion;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
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
