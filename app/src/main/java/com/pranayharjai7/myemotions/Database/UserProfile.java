package com.pranayharjai7.myemotions.Database;

import java.util.List;

public class UserProfile {

    private String username;
    private String email;
    private List<Emotion> emotions;
    private String location;
    private List<String> friends;
    private String moodVisibility;

    public UserProfile() {
    }

    public UserProfile(String username, String email, List<Emotion> emotions, String location, List<String> friends, String moodVisibility) {
        this.username = username;
        this.email = email;
        this.emotions = emotions;
        this.location = location;
        this.friends = friends;
        this.moodVisibility = moodVisibility;
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

    public List<Emotion> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<Emotion> emotions) {
        this.emotions = emotions;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getMoodVisibility() {
        return moodVisibility;
    }

    public void setMoodVisibility(String moodVisibility) {
        this.moodVisibility = moodVisibility;
    }
}
