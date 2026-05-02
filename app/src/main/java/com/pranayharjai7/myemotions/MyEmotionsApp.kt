package com.pranayharjai7.myemotions

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyEmotionsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Supabase initialization will happen here or in a manager
    }
}
