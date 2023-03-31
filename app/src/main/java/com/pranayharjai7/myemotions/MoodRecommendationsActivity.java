package com.pranayharjai7.myemotions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pranayharjai7.myemotions.Utils.Adapters.MoodRecommendationsViewAdapter;
import com.pranayharjai7.myemotions.databinding.ActivityMoodRecommendationsBinding;

import java.util.ArrayList;
import java.util.List;

public class MoodRecommendationsActivity extends AppCompatActivity {

    private ActivityMoodRecommendationsBinding binding;
    private List<String> suggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoodRecommendationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        suggestions = populateSuggestions();
        binding.moodRecommendationsRecyclerView.setAdapter(new MoodRecommendationsViewAdapter(suggestions, this));
    }

    private List<String> populateSuggestions() {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Listen to a happy song. Put on a happy playlist\n\n (tap to open spotify)");
        suggestions.add("Dress up. Wearing attractive clothes might cheer you up.");
        suggestions.add("Meditate. Take control of your spare time to reduce stress.");
        suggestions.add("Exercise. Doing exercise can release chemicals in your brain to lift your mood.");
        return suggestions;
    }
}