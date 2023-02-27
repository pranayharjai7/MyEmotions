package com.pranayharjai7.myemotions.Utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FriendsJSONConverter {

    @TypeConverter
    public String fromFriendsList(List<String> friends) {
        if (friends == null) {
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        String json = gson.toJson(friends, type);
        return json;
    }

    @TypeConverter
    public List<String> toFriendsList(String friendsString) {
        if (friendsString == null) {
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> friends = gson.fromJson(friendsString, type);
        return friends;
    }
}
