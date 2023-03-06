package com.pranayharjai7.myemotions.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pranayharjai7.myemotions.Database.Emotion;

import java.util.List;

@Dao
public interface EmotionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewEmotion(Emotion emotion);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllEmotions(List<Emotion> emotions);

    @Query("SELECT * FROM Emotion")
    LiveData<List<Emotion>> getAllEmotions();

    @Query("SELECT * FROM Emotion WHERE userId = :userId")
    LiveData<List<Emotion>> getUserEmotions(String userId);

    @Query("DELETE FROM Emotion")
    void clearAllData();

    @Query("DELETE FROM Emotion WHERE userId = :userId")
    void clearUserData(String userId);
}
