package com.pranayharjai7.myemotions.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pranayharjai7.myemotions.Utils.Enums.MoodVisibility;

@Entity(tableName = "UserProfile")
public class UserProfile {

    @PrimaryKey
    @NonNull
    private String userId;
    private String username;
    private String email;
    private String location;
    private String moodVisibility;
    private String latestEmotion;
    private String latestEmotionDateTime;

    public UserProfile() {
    }

    public UserProfile(@NonNull String userId, String username, String email, String location, String moodVisibility, String latestEmotion, String latestEmotionDateTime) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.location = location;
        this.moodVisibility = moodVisibility;
        this.latestEmotion = latestEmotion;
        this.latestEmotionDateTime = latestEmotionDateTime;
    }

    public UserProfile(@NonNull String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.location = "";
        this.moodVisibility = MoodVisibility.PUBLIC.toString();
        this.latestEmotion = "";
        this.latestEmotionDateTime = "";
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMoodVisibility() {
        return moodVisibility;
    }

    public void setMoodVisibility(String moodVisibility) {
        this.moodVisibility = moodVisibility;
    }

    public String getLatestEmotion() {
        return latestEmotion;
    }

    public void setLatestEmotion(String latestEmotion) {
        this.latestEmotion = latestEmotion;
    }

    public String getLatestEmotionDateTime() {
        return latestEmotionDateTime;
    }

    public void setLatestEmotionDateTime(String latestEmotionDateTime) {
        this.latestEmotionDateTime = latestEmotionDateTime;
    }
}
