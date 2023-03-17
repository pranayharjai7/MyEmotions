package com.pranayharjai7.myemotions.Database.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.pranayharjai7.myemotions.Database.UserProfile;

@Database(entities = {UserProfile.class}, version = 1, exportSchema = false)
public abstract class UserProfileDatabase extends RoomDatabase {
    public abstract UserProfileDAO userProfileDAO();
}
