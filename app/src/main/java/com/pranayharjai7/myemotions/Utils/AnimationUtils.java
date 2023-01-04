package com.pranayharjai7.myemotions.Utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.pranayharjai7.myemotions.databinding.ActivityMainBinding;


public class AnimationUtils {

    public static void animateOpenRecordEmotionButton(ActivityMainBinding binding) {

        binding.cameraButton.setVisibility(View.VISIBLE);
        binding.galleryButton.setVisibility(View.VISIBLE);
        ObjectAnimator animateCameraX= ObjectAnimator.ofFloat(binding.cameraButton, "translationX", 200);
        ObjectAnimator animateCameraY= ObjectAnimator.ofFloat(binding.cameraButton, "translationY", -230);
        ObjectAnimator animateGalleryX= ObjectAnimator.ofFloat(binding.galleryButton, "translationX", -200);
        ObjectAnimator animateGalleryY= ObjectAnimator.ofFloat(binding.galleryButton, "translationY", -230);
        ObjectAnimator animateRecordEmotionRotation= ObjectAnimator.ofFloat(binding.recordEmotionButton, "rotation", 45);
        animateCameraX.setDuration(200).start();
        animateCameraY.setDuration(200).start();
        animateGalleryX.setDuration(200).start();
        animateGalleryY.setDuration(200).start();
        animateRecordEmotionRotation.setDuration(200).start();

        animateCameraX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.cameraTextView.setVisibility(View.VISIBLE);
                binding.galleryTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public static void animateCloseRecordEmotionButton(ActivityMainBinding binding) {

        binding.cameraTextView.setVisibility(View.GONE);
        binding.galleryTextView.setVisibility(View.GONE);
        ObjectAnimator animateCameraX= ObjectAnimator.ofFloat(binding.cameraButton, "translationX", 0);
        ObjectAnimator animateCameraY= ObjectAnimator.ofFloat(binding.cameraButton, "translationY", 0);
        ObjectAnimator animateGalleryX= ObjectAnimator.ofFloat(binding.galleryButton, "translationX", 0);
        ObjectAnimator animateGalleryY= ObjectAnimator.ofFloat(binding.galleryButton, "translationY", 0);
        ObjectAnimator animateRecordEmotionRotation= ObjectAnimator.ofFloat(binding.recordEmotionButton, "rotation", 0);
        animateCameraX.setDuration(200).start();
        animateCameraY.setDuration(200).start();
        animateGalleryX.setDuration(200).start();
        animateGalleryY.setDuration(200).start();
        animateRecordEmotionRotation.setDuration(200).start();

        animateCameraX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.cameraButton.setVisibility(View.GONE);
                binding.galleryButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
