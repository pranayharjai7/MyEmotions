package com.pranayharjai7.myemotions.Fragments.MainActivityFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.Emotion;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.DateTimeUtils;
import com.pranayharjai7.myemotions.Utils.EmotionColorUtils;
import com.pranayharjai7.myemotions.Utils.Interfaces.Callback;
import com.pranayharjai7.myemotions.databinding.FragmentStatsBinding;
import com.pranayharjai7.myemotions.mtcnn.EmotionLabelUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    public StatsFragment() {
        super(R.layout.fragment_stats);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        getAllUserEmotionsFromFirebase(userEmotions -> {
            Map<String, Integer> currentWeekEmotionsMap = calculateFrequencyOfEmotionsInAWeek(userEmotions);
            if (isAdded()) {
                createBarChart(currentWeekEmotionsMap);
            }
        });
    }

    private void getAllUserEmotionsFromFirebase(Callback<List<Emotion>> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("emotions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Emotion> emotions = new ArrayList<>();
                        for (DataSnapshot emotionSnapshot : snapshot.getChildren()) {
                            String dateTime = emotionSnapshot.getKey();
                            String recordedEmotion = emotionSnapshot.child("emotion").getValue(String.class);
                            Emotion emotion = new Emotion();
                            emotion.setUserId(mAuth.getCurrentUser().getUid());
                            emotion.setDateTime(dateTime);
                            emotion.setEmotion(recordedEmotion);
                            emotions.add(emotion);
                        }
                        callback.onSuccess(emotions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressWarnings("ConstantConditions")
    private Map<String, Integer> calculateFrequencyOfEmotionsInAWeek(List<Emotion> userEmotions) {
        List<Emotion> currentWeekEmotions = getListOfEmotionsForCurrentWeek(userEmotions);

        Map<String, Integer> frequencyOfEmotions = new HashMap<>();
        for (Emotion emotion : currentWeekEmotions) {
            String recordedEmotion = emotion.getEmotion();
            if (frequencyOfEmotions.containsKey(recordedEmotion)) {
                int currentFrequency = frequencyOfEmotions.get(recordedEmotion);
                frequencyOfEmotions.put(recordedEmotion, currentFrequency + 1);
            } else {
                frequencyOfEmotions.put(recordedEmotion, 1);
            }
        }
        return frequencyOfEmotions;
    }

    private List<Emotion> getListOfEmotionsForCurrentWeek(List<Emotion> userEmotions) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return userEmotions.stream()
                .filter(emotion -> {
                    LocalDateTime emotionDateTime = DateTimeUtils.convertStringToLocalDateTime(emotion.getDateTime());
                    return emotionDateTime.isBefore(currentDateTime) && emotionDateTime.isAfter(currentDateTime.minusWeeks(1));
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("ConstantConditions")
    private void createBarChart(Map<String, Integer> currentWeekEmotions) {
        List<String> emotionLabels = EmotionLabelUtils.loadLabels(requireContext());
        List<Integer> colors = EmotionColorUtils.getColorsForEmotions(emotionLabels);

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < emotionLabels.size(); i++) {
            String emotion = emotionLabels.get(i);
            int frequency = currentWeekEmotions.getOrDefault(emotion, 0);
            entries.add(new BarEntry(i, frequency));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Frequency of Emotions");
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        binding.frequencyOfEmotionsBarChart.setData(barData);

        binding.frequencyOfEmotionsBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.frequencyOfEmotionsBarChart.getXAxis().setGranularity(1f);
        binding.frequencyOfEmotionsBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(emotionLabels));

        binding.frequencyOfEmotionsBarChart.getAxisLeft().setAxisMinimum(0f);

        binding.frequencyOfEmotionsBarChart.setDrawBarShadow(false);
        binding.frequencyOfEmotionsBarChart.setPinchZoom(true);
        binding.frequencyOfEmotionsBarChart.setDrawGridBackground(false);
        binding.frequencyOfEmotionsBarChart.setScaleEnabled(false);
        binding.frequencyOfEmotionsBarChart.getDescription().setEnabled(false);
        binding.frequencyOfEmotionsBarChart.getLegend().setEnabled(false);
        binding.frequencyOfEmotionsBarChart.animateY(1000);
        binding.frequencyOfEmotionsBarChart.invalidate();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}