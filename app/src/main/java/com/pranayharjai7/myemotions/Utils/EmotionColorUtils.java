package com.pranayharjai7.myemotions.Utils;

import android.content.Context;
import android.graphics.Color;

import com.pranayharjai7.myemotions.mtcnn.EmotionLabelUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmotionColorUtils {

    private static final String HAPPINESS_COLOR = "#049943";
    private static final String SURPRISE_COLOR = "#1ecf92";
    private static final String CONTEMPT_COLOR = "#bca67f";
    private static final String NEUTRAL_COLOR = "#d4ab36";
    private static final String FEAR_COLOR = "#8464e4";
    private static final String SADNESS_COLOR = "#d68838";
    private static final String ANGER_COLOR = "#e94339";
    private static final String DISGUST_COLOR = "#bb1b34";

    public static List<Integer> getColorsForEmotions(Context context) {

        List<String> emotionLabels = EmotionLabelUtils.loadLabels(context);
        return getColorsForEmotions(emotionLabels);
    }

    public static List<Integer> getColorsForEmotions(List<String> emotionLabels) {
        List<Integer> colors = new ArrayList<>();
        Map<String, Integer> emotionColorMap = getColorMapForEmotions(emotionLabels);

        for (String emotionLabel : emotionLabels) {
            colors.add(emotionColorMap.get(emotionLabel));
        }
        return colors;
    }

    public static Map<String, Integer> getColorMapForEmotions(Context context) {
        List<String> emotionLabels = EmotionLabelUtils.loadLabels(context);
        return getColorMapForEmotions(emotionLabels);
    }

    public static Map<String, Integer> getColorMapForEmotions(List<String> emotionLabels) {
        Map<String, Integer> emotionColorMap = new HashMap<>();

        for (String emotionLabel : emotionLabels) {
            switch (emotionLabel) {
                case "Happiness":
                    emotionColorMap.put(emotionLabel, Color.parseColor(HAPPINESS_COLOR));
                    break;
                case "Surprise":
                    emotionColorMap.put(emotionLabel, Color.parseColor(SURPRISE_COLOR));
                    break;
                case "Contempt":
                    emotionColorMap.put(emotionLabel, Color.parseColor(CONTEMPT_COLOR));
                    break;
                case "Neutral":
                    emotionColorMap.put(emotionLabel, Color.parseColor(NEUTRAL_COLOR));
                    break;
                case "Fear":
                    emotionColorMap.put(emotionLabel, Color.parseColor(FEAR_COLOR));
                    break;
                case "Sadness":
                    emotionColorMap.put(emotionLabel, Color.parseColor(SADNESS_COLOR));
                    break;
                case "Anger":
                    emotionColorMap.put(emotionLabel, Color.parseColor(ANGER_COLOR));
                    break;
                case "Disgust":
                    emotionColorMap.put(emotionLabel, Color.parseColor(DISGUST_COLOR));
                    break;
                default:
                    emotionColorMap.put(emotionLabel, Color.GRAY);
            }
        }
        return emotionColorMap;
    }
}
