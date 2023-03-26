package com.pranayharjai7.myemotions.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FriendRequest")
public class FriendRequest {

    @PrimaryKey
    @NonNull
    private String receiverUserId;
    private String FriendRequestSenderId;

    public FriendRequest() {
    }

    public FriendRequest(@NonNull String receiverUserId, String friendRequestSenderId) {
        this.receiverUserId = receiverUserId;
        FriendRequestSenderId = friendRequestSenderId;
    }

    @NonNull
    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(@NonNull String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getFriendRequestSenderId() {
        return FriendRequestSenderId;
    }

    public void setFriendRequestSenderId(String friendRequestSenderId) {
        FriendRequestSenderId = friendRequestSenderId;
    }
}
