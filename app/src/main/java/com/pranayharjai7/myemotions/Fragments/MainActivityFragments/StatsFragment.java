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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
import java.time.LocalTime;
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
            Map<String, Integer> currentDayEmotionsMap = calculateFrequencyOfEmotionsInADay(userEmotions);
            Map<String, Integer> currentWeekEmotionsMap = calculateFrequencyOfEmotionsInAWeek(userEmotions);
            Map<String, Integer> currentMonthEmotionsMap = calculateFrequencyOfEmotionsInAMonth(userEmotions);
            if (isAdded()) {
                createPieChartForCurrentDay(currentDayEmotionsMap);
                createBarChartForCurrentWeek(currentWeekEmotionsMap);
                createBarChartForCurrentMonth(currentMonthEmotionsMap);
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

    private Map<String, Integer> calculateFrequencyOfEmotionsInADay(List<Emotion> userEmotions) {
        List<Emotion> currentDayEmotions = getListOfEmotionsForCurrentDay(userEmotions);

        Map<String, Integer> frequencyOfEmotions = new HashMap<>();
        for (Emotion emotion : currentDayEmotions) {
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

    private Map<String, Integer> calculateFrequencyOfEmotionsInAMonth(List<Emotion> userEmotions) {
        List<Emotion> currentMonthEmotions = getListOfEmotionsForCurrentMonth(userEmotions);

        Map<String, Integer> frequencyOfEmotions = new HashMap<>();
        for (Emotion emotion : currentMonthEmotions) {
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

    private List<Emotion> getListOfEmotionsForCurrentDay(List<Emotion> userEmotions) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDateTime.of(currentDateTime.toLocalDate(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(currentDateTime.toLocalDate(), LocalTime.MAX);
        return userEmotions.stream()
                .filter(emotion -> {
                    LocalDateTime emotionDateTime = DateTimeUtils.convertStringToLocalDateTime(emotion.getDateTime());
                    return emotionDateTime.isBefore(endOfDay) && emotionDateTime.isAfter(startOfDay);
                })
                .collect(Collectors.toList());
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

    private List<Emotion> getListOfEmotionsForCurrentMonth(List<Emotion> userEmotions) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startOfMonth = LocalDateTime.of(currentDateTime.toLocalDate().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime endOfMonth = LocalDateTime.of(currentDateTime.toLocalDate().withDayOfMonth(currentDateTime.toLocalDate().lengthOfMonth()), LocalTime.MAX);
        return userEmotions.stream()
                .filter(emotion -> {
                    LocalDateTime emotionDateTime = DateTimeUtils.convertStringToLocalDateTime(emotion.getDateTime());
                    return emotionDateTime.isBefore(endOfMonth) && emotionDateTime.isAfter(startOfMonth);
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("ConstantConditions")
    private void createPieChartForCurrentDay(Map<String, Integer> currentDayEmotionsMap) {
        List<String> emotionLabels = EmotionLabelUtils.loadLabelsSortedByValence(requireContext());
        List<Integer> colors = EmotionColorUtils.getColorsForEmotions(emotionLabels);
        List<PieEntry> entries = new ArrayList<>();

        for (String emotion : emotionLabels) {
            int frequency = currentDayEmotionsMap.getOrDefault(emotion, 0);
            entries.add(new PieEntry(frequency, emotion));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Range of Emotions");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);
        binding.rangeOfEmotionsPieChart.setData(pieData);
        binding.rangeOfEmotionsPieChart.getDescription().setEnabled(false);
        binding.rangeOfEmotionsPieChart.animateY(1000);
        binding.rangeOfEmotionsPieChart.invalidate();
    }

    @SuppressWarnings("ConstantConditions")
    private void createBarChartForCurrentWeek(Map<String, Integer> currentWeekEmotions) {
        List<String> emotionLabels = EmotionLabelUtils.loadLabelsSortedByValence(requireContext());
        List<Integer> colors = EmotionColorUtils.getColorsForEmotions(emotionLabels);

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < emotionLabels.size(); i++) {
            String emotion = emotionLabels.get(i);
            int frequency = currentWeekEmotions.getOrDefault(emotion, 0);
            entries.add(new BarEntry(i, frequency));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Frequency of Emotions (Current week)");
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        binding.frequencyOfEmotionsWeekBarChart.setData(barData);

        binding.frequencyOfEmotionsWeekBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.frequencyOfEmotionsWeekBarChart.getXAxis().setGranularity(1f);
        binding.frequencyOfEmotionsWeekBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(emotionLabels));

        binding.frequencyOfEmotionsWeekBarChart.getAxisLeft().setAxisMinimum(0f);

        binding.frequencyOfEmotionsWeekBarChart.setDrawBarShadow(false);
        binding.frequencyOfEmotionsWeekBarChart.setPinchZoom(true);
        binding.frequencyOfEmotionsWeekBarChart.setDrawGridBackground(false);
        binding.frequencyOfEmotionsWeekBarChart.setScaleEnabled(false);
        binding.frequencyOfEmotionsWeekBarChart.getDescription().setEnabled(false);
        binding.frequencyOfEmotionsWeekBarChart.getLegend().setEnabled(false);
        binding.frequencyOfEmotionsWeekBarChart.animateY(1000);
        binding.frequencyOfEmotionsWeekBarChart.invalidate();
    }


    @SuppressWarnings("ConstantConditions")
    private void createBarChartForCurrentMonth(Map<String, Integer> currentMonthEmotionsMap) {
        List<String> emotionLabels = EmotionLabelUtils.loadLabelsSortedByValence(requireContext());
        List<Integer> colors = EmotionColorUtils.getColorsForEmotions(emotionLabels);

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < emotionLabels.size(); i++) {
            String emotion = emotionLabels.get(i);
            int frequency = currentMonthEmotionsMap.getOrDefault(emotion, 0);
            entries.add(new BarEntry(i, frequency));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Frequency of Emotions (Current month)");
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        binding.frequencyOfEmotionsMonthBarChart.setData(barData);

        binding.frequencyOfEmotionsMonthBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.frequencyOfEmotionsMonthBarChart.getXAxis().setGranularity(1f);
        binding.frequencyOfEmotionsMonthBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(emotionLabels));

        binding.frequencyOfEmotionsMonthBarChart.getAxisLeft().setAxisMinimum(0f);

        binding.frequencyOfEmotionsMonthBarChart.setDrawBarShadow(false);
        binding.frequencyOfEmotionsMonthBarChart.setPinchZoom(true);
        binding.frequencyOfEmotionsMonthBarChart.setDrawGridBackground(false);
        binding.frequencyOfEmotionsMonthBarChart.setScaleEnabled(false);
        binding.frequencyOfEmotionsMonthBarChart.getDescription().setEnabled(false);
        binding.frequencyOfEmotionsMonthBarChart.getLegend().setEnabled(false);
        binding.frequencyOfEmotionsMonthBarChart.animateY(1000);
        binding.frequencyOfEmotionsMonthBarChart.invalidate();
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