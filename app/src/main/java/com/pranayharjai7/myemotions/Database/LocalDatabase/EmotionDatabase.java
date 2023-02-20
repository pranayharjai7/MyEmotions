package com.pranayharjai7.myemotions.Database.LocalDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Emotion.class}, version = 1, exportSchema = false)
public abstract class EmotionDatabase extends RoomDatabase {
    public abstract EmotionDAO emotionDAO();
}
