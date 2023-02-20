package com.pranayharjai7.myemotions.Utils.Adapters;

import static com.pranayharjai7.myemotions.R.drawable.anger;
import static com.pranayharjai7.myemotions.R.drawable.contempt;
import static com.pranayharjai7.myemotions.R.drawable.disgust;
import static com.pranayharjai7.myemotions.R.drawable.fear;
import static com.pranayharjai7.myemotions.R.drawable.happiness;
import static com.pranayharjai7.myemotions.R.drawable.neutral;
import static com.pranayharjai7.myemotions.R.drawable.sadness;
import static com.pranayharjai7.myemotions.R.drawable.surprise;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pranayharjai7.myemotions.Database.LocalDatabase.Expression;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.databinding.ExpressionCardLayoutBinding;

import java.time.LocalDateTime;
import java.util.List;

public class ExpressionViewAdapter extends RecyclerView.Adapter<ExpressionViewAdapter.ExpressionViewHolder> {

    private List<Expression> expressions;

    public ExpressionViewAdapter(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public static class ExpressionViewHolder extends RecyclerView.ViewHolder {
        ExpressionCardLayoutBinding binding;

        public ExpressionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ExpressionCardLayoutBinding.bind(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return expressions.size();
    }

    @NonNull
    @Override
    public ExpressionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expression_card_layout, parent, false);
        return new ExpressionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpressionViewHolder holder, int position) {
        LocalDateTime dateTime = LocalDateTime.parse(expressions.get(position).getDateTime());
        String expression = expressions.get(position).getExpression();
        String dateTimeString = getDateAndTimeAsString(dateTime);
        setEmojiAndColor(expression, holder);
        holder.binding.dateTextView.setText(dateTimeString);
        holder.binding.expressionsTextView.setText(expression);
    }

    private String getDateAndTimeAsString(LocalDateTime dateTime) {
        StringBuilder resultDateTime = new StringBuilder();
        resultDateTime.append("")
                .append(dateTime.getDayOfMonth())
                .append(".")
                .append(dateTime.getMonthValue())
                .append(".")
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

        resultDateTime.append(" ")
                .append(hour)
                .append(":")
                .append(minute)
                .append(amPm);
        return resultDateTime.toString();
    }

    private void setEmojiAndColor(String expression, ExpressionViewHolder holder) {
        switch (expression) {
            case "Anger": {
                holder.binding.emojiImageView.setImageResource(anger);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#e94339"));
                break;
            }
            case "Contempt": {
                holder.binding.emojiImageView.setImageResource(contempt);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#bca67f"));
                break;
            }
            case "Disgust": {
                holder.binding.emojiImageView.setImageResource(disgust);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#bb1b34"));
                break;
            }
            case "Fear": {
                holder.binding.emojiImageView.setImageResource(fear);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#8464e4"));
                break;
            }
            case "Happiness": {
                holder.binding.emojiImageView.setImageResource(happiness);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#049943"));
                break;
            }
            case "Neutral": {
                holder.binding.emojiImageView.setImageResource(neutral);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#d4ab36"));
                break;
            }
            case "Sadness": {
                holder.binding.emojiImageView.setImageResource(sadness);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#d68838"));
                break;
            }
            case "Surprise": {
                holder.binding.emojiImageView.setImageResource(surprise);
                holder.binding.homeEmotionCardView.setCardBackgroundColor(Color.parseColor("#1ecf92"));
                break;
            }
            default : {
                holder.binding.emojiImageView.setImageResource(neutral);
            }
        }
    }
}
