package com.pranayharjai7.myemotions.Utils.Adapters;

import static com.pranayharjai7.myemotions.R.drawable.anger;
import static com.pranayharjai7.myemotions.R.drawable.contempt;
import static com.pranayharjai7.myemotions.R.drawable.disgust;
import static com.pranayharjai7.myemotions.R.drawable.fear;
import static com.pranayharjai7.myemotions.R.drawable.happiness;
import static com.pranayharjai7.myemotions.R.drawable.neutral;
import static com.pranayharjai7.myemotions.R.drawable.sadness;
import static com.pranayharjai7.myemotions.R.drawable.surprise;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pranayharjai7.myemotions.Database.Emotion;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.DateTimeUtils;
import com.pranayharjai7.myemotions.Utils.EmotionColorUtils;
import com.pranayharjai7.myemotions.databinding.EmotionCardLayoutBinding;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EmotionViewAdapter extends RecyclerView.Adapter<EmotionViewAdapter.EmotionViewHolder> {

    private List<Emotion> emotions;
    private Context context;

    public EmotionViewAdapter(List<Emotion> emotions, Context context) {
        this.emotions = emotions;
        this.context = context;
    }

    public static class EmotionViewHolder extends RecyclerView.ViewHolder {
        EmotionCardLayoutBinding binding;

        public EmotionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = EmotionCardLayoutBinding.bind(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return emotions.size();
    }

    @NonNull
    @Override
    public EmotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emotion_card_layout, parent, false);
        return new EmotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmotionViewHolder holder, int position) {
        LocalDateTime dateTime = DateTimeUtils.convertStringToLocalDateTime(emotions.get(position).getDateTime());
        String emotion = emotions.get(position).getEmotion();
        String dateTimeString = getDateAndTimeAsString(dateTime);
        setEmojiAndColor(emotion, holder);
        holder.binding.dateTextView.setText(dateTimeString);
        holder.binding.emotionTextView.setText(emotion);
    }

    private String getDateAndTimeAsString(LocalDateTime dateTime) {
        StringBuilder resultDateTime = new StringBuilder();
        resultDateTime.append("")
                .append(dateTime.getDayOfMonth())
                .append(" ")
                .append(dateTime.getMonth())
                .append(" ")
                .append(dateTime.getYear());

        int hour = dateTime.getHour();
        String amPm = "am";
        if (hour > 12) {
            hour = hour - 12;
            amPm = "pm";
        }
        int min = dateTime.getMinute();
        String minute = "" + min;
        if (min < 10) {
            minute = "0" + min;
        }

        resultDateTime.append("\n       ")
                .append(hour)
                .append(":")
                .append(minute)
                .append(" ")
                .append(amPm);
        return resultDateTime.toString();
    }

    @SuppressWarnings("ConstantConditions")
    private void setEmojiAndColor(String emotion, EmotionViewHolder holder) {
        Map<String, Integer> emotionColorMap = EmotionColorUtils.getColorMapForEmotions(context);
        holder.binding.homeEmotionCardView.setCardBackgroundColor(emotionColorMap.get(emotion));

        switch (emotion) {
            case "Anger": {
                holder.binding.emojiImageView.setImageResource(anger);
                break;
            }
            case "Contempt": {
                holder.binding.emojiImageView.setImageResource(contempt);
                break;
            }
            case "Disgust": {
                holder.binding.emojiImageView.setImageResource(disgust);
                break;
            }
            case "Fear": {
                holder.binding.emojiImageView.setImageResource(fear);
                break;
            }
            case "Happiness": {
                holder.binding.emojiImageView.setImageResource(happiness);
                break;
            }
            case "Neutral": {
                holder.binding.emojiImageView.setImageResource(neutral);
                break;
            }
            case "Sadness": {
                holder.binding.emojiImageView.setImageResource(sadness);
                break;
            }
            case "Surprise": {
                holder.binding.emojiImageView.setImageResource(surprise);
                break;
            }
            default: {
                holder.binding.emojiImageView.setImageResource(neutral);
            }
        }
    }
}
