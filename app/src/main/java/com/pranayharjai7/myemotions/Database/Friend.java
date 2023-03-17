package com.pranayharjai7.myemotions.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Friend")
public class Friend {

    @PrimaryKey
    @NonNull
    private String UserId;
    private String FriendId;

    public Friend() {
    }

    public Friend(@NonNull String userId, String friendId) {
        UserId = userId;
        FriendId = friendId;
    }

    @NonNull
    public String getUserId() {
        return UserId;
    }

    public void setUserId(@NonNull String userId) {
        UserId = userId;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }
}
