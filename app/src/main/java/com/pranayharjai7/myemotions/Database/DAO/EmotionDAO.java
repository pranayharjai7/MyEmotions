package com.pranayharjai7.myemotions.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.pranayharjai7.myemotions.Database.Emotion;

import java.util.List;

@Dao
public interface EmotionDAO {

    @Insert
    void insertNewEmotion(Emotion emotion);

    @Query("SELECT * FROM Emotion")
    LiveData<List<Emotion>> getAllEmotion();

    @Query("DELETE FROM Emotion")
    void clearData();
}
