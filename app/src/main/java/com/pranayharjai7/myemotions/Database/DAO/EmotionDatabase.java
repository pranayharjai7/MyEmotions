package com.pranayharjai7.myemotions.Database.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.pranayharjai7.myemotions.Database.Emotion;

@Database(entities = {Emotion.class}, version = 1, exportSchema = false)
public abstract class EmotionDatabase extends RoomDatabase {
    public abstract EmotionDAO emotionDAO();
}
