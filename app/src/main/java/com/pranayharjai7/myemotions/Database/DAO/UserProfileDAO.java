package com.pranayharjai7.myemotions.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pranayharjai7.myemotions.Database.UserProfile;

import java.util.List;

@Dao
public interface UserProfileDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewUserProfile(UserProfile userProfile);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllUserProfiles(List<UserProfile> userProfiles);

    @Query("SELECT * FROM UserProfile")
    LiveData<List<UserProfile>> getAllUserProfile();

    @Query("DELETE FROM UserProfile")
    void clearData();
}
