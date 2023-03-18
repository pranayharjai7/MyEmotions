package com.pranayharjai7.myemotions.Database.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.pranayharjai7.myemotions.Database.Friend;

@Database(entities = {Friend.class}, version = 1, exportSchema = false)
public abstract class FriendDatabase extends RoomDatabase {

    public abstract FriendDAO friendDAO();
}
