package com.pranayharjai7.myemotions.Database.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.Utils.EmotionJSONConverter;
import com.pranayharjai7.myemotions.Utils.FriendsJSONConverter;

@Database(entities = {UserProfile.class}, version = 1, exportSchema = false)
@TypeConverters({EmotionJSONConverter.class, FriendsJSONConverter.class})
public abstract class UserProfileDatabase extends RoomDatabase {
    public abstract UserProfileDAO userProfileDAO();
}
