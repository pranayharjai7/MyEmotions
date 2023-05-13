package com.pranayharjai7.myemotions.Utils.Adapters;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.databinding.MoodRecommendationCardLayoutBinding;

import java.util.List;

public class MoodRecommendationsViewAdapter extends RecyclerView.Adapter<MoodRecommendationsViewAdapter.MoodRecommendationsViewHolder> {

    private List<String> suggestions;
    private Context context;

    public MoodRecommendationsViewAdapter(List<String> suggestions, Context context) {
        this.suggestions = suggestions;
        this.context = context;
    }

    public static class MoodRecommendationsViewHolder extends RecyclerView.ViewHolder {
        MoodRecommendationCardLayoutBinding binding;

        public MoodRecommendationsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MoodRecommendationCardLayoutBinding.bind(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    @NonNull
    @Override
    public MoodRecommendationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mood_recommendation_card_layout, parent, false);
        return new MoodRecommendationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodRecommendationsViewHolder holder, int position) {
        String header = suggestions.get(position).split("\\.")[0];
        String description = suggestions.get(position).split("\\.")[1];
        holder.binding.suggestionsHeaderTextView.setText(header);
        holder.binding.suggestionsDescriptionTextView.setText(description);
        
        if(header.startsWith("Listen to a happy")) {
            holder.binding.moodRecommendationRecyclerCardView.setOnClickListener(v -> {
                openSpotify();
            });
        }
    }

    private void openSpotify() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.MainActivity"));
        intent.putExtra(SearchManager.QUERY, "Happy Songs" );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
