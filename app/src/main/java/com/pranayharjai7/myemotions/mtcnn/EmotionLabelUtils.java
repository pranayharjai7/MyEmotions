package com.pranayharjai7.myemotions.mtcnn;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EmotionLabelUtils {
    public static List<String> loadLabels(Context context) {
        List<String> labels = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("emotionsLabel.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                String[] categoryInfo = line.trim().split(":");
                String category = categoryInfo[1];
                labels.add(category);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading emotion label file!", e);
        }
        return labels;
    }
}
