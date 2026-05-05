package com.pranayharjai7.myemotions.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.pranayharjai7.myemotions.data.worker.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun scheduleReminder(title: String, message: String, delayMinutes: Long = 1) {
        val inputData = workDataOf(
            ReminderWorker.KEY_TITLE to title,
            ReminderWorker.KEY_MESSAGE to message
        )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "Reminder_$title",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelAllWork()
    }
}
