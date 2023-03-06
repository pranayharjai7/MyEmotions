package com.pranayharjai7.myemotions.Utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pranayharjai7.myemotions.Database.Emotion;

import java.lang.reflect.Type;
import java.util.List;

public class EmotionJSONConverter {

    @TypeConverter
    public String fromEmotionsList(List<Emotion> emotions) {
        if (emotions == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Emotion>>() {
        }.getType();
        String json = gson.toJson(emotions, type);
        return json;
    }

    @TypeConverter
    public List<Emotion> toEmotionsList(String emotionsString) {
        if (emotionsString == null) {
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Emotion>>() {
        }.getType();
        List<Emotion> emotions = gson.fromJson(emotionsString, type);
        return emotions;
    }
}
