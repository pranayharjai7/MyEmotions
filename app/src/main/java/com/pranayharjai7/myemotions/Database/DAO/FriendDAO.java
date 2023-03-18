package com.pranayharjai7.myemotions.Database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pranayharjai7.myemotions.Database.Friend;

import java.util.List;

@Dao
public interface FriendDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewFriend(Friend friend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllFriends(List<Friend> friends);

    @Query("SELECT * FROM Friend")
    LiveData<List<Friend>> getAllFriends();

    @Query("DELETE FROM Friend")
    void clearData();
}
