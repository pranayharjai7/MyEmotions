package com.pranayharjai7.myemotions.Fragments.MainActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.pranayharjai7.myemotions.Database.LocalDatabase.ExpressionDatabase;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.Adapters.ExpressionViewAdapter;
import com.pranayharjai7.myemotions.ViewModels.HomeViewModel;
import com.pranayharjai7.myemotions.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ExpressionDatabase expressionDatabase;
    private String clear = new String();

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        observations();
    }

    private void init() {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        expressionDatabase = Room.databaseBuilder(getView().getContext(), ExpressionDatabase.class, "Expression_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    private void observations() {
        homeViewModel.getEmotionPic().observe(getViewLifecycleOwner(), bitmap -> {
            binding.emotionsImageView.setImageBitmap(bitmap);
        });

        expressionDatabase.expressionDAO().getAllExpression().observe(getViewLifecycleOwner(), expressions -> {
            binding.emotionsRecyclerView.setAdapter(new ExpressionViewAdapter(expressions));
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
